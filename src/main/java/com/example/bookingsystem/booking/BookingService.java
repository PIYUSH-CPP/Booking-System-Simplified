package com.example.bookingsystem.booking;

import com.example.bookingsystem.common.ConflictException;
import com.example.bookingsystem.common.ResourceNotFoundException;
import com.example.bookingsystem.unit.Unit;
import com.example.bookingsystem.unit.UnitRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.util.List;

@Service
public class BookingService {
    private final BookingRepository bookings;
    private final UnitRepository units;

    public BookingService(BookingRepository bookings, UnitRepository units) {
        this.bookings = bookings;
        this.units = units;
    }

    @Transactional(readOnly = true)
    public List<Booking> getAll(String customerName) {
        return customerName == null || customerName.isBlank()
                ? bookings.findAll()
                : bookings.findByCustomerNameIgnoreCase(customerName.trim());
    }

    @Transactional(readOnly = true)
    public Booking getById(Long id) {
        return bookings.findById(id).orElseThrow(() -> new ResourceNotFoundException("Booking not found: " + id));
    }

    @Transactional
    public Booking create(Long unitId, String customerName, LocalDate startDate, LocalDate endDate) {
        if (startDate == null || endDate == null || !endDate.isAfter(startDate)) {
            throw new IllegalArgumentException("End date must be after start date.");
        }
        // Lock the unit row so concurrent requests for the same unit run one at a time.
        // This closes the check-then-insert race condition in the overlap check below.
        Unit unit = units.findByIdForUpdate(unitId)
                .orElseThrow(() -> new ResourceNotFoundException("Unit not found: " + unitId));

        boolean overlap = bookings.existsByUnitIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                unitId, BookingStatus.CONFIRMED, endDate, startDate);
        if (overlap) throw new ConflictException("Unit is already booked for the selected dates.");

        Booking booking = new Booking();
        booking.setUnit(unit);
        booking.setCustomerName(customerName == null ? null : customerName.trim());
        booking.setStartDate(startDate);
        booking.setEndDate(endDate);
        booking.setStatus(BookingStatus.CONFIRMED);
        return bookings.save(booking);
    }

    @Transactional
    public Booking cancel(Long id) {
        Booking booking = getById(id);
        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new ConflictException("Booking is already cancelled: " + id);
        }
        booking.setStatus(BookingStatus.CANCELLED);
        return bookings.save(booking);
    }
}
