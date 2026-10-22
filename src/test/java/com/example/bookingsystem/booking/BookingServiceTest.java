package com.example.bookingsystem.booking;

import com.example.bookingsystem.common.ConflictException;
import com.example.bookingsystem.common.ResourceNotFoundException;
import com.example.bookingsystem.unit.Unit;
import com.example.bookingsystem.unit.UnitRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock BookingRepository bookings;
    @Mock UnitRepository units;
    @InjectMocks BookingService service;

    private final LocalDate start = LocalDate.of(2026, 11, 1);
    private final LocalDate end = LocalDate.of(2026, 11, 4);
    private Unit unit;

    @BeforeEach
    void setUp() {
        unit = new Unit("City View", "Jaipur", 2, new BigDecimal("2500.00"));
        unit.setId(1L);
    }

    @Test
    void createsConfirmedBookingWhenDatesAreFree() {
        when(units.findByIdForUpdate(1L)).thenReturn(Optional.of(unit));
        when(bookings.existsByUnitIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                1L, BookingStatus.CONFIRMED, end, start)).thenReturn(false);
        when(bookings.save(any(Booking.class))).thenAnswer(inv -> inv.getArgument(0));

        Booking result = service.create(1L, "  Piyush ", start, end);

        assertEquals(BookingStatus.CONFIRMED, result.getStatus());
        assertEquals("Piyush", result.getCustomerName());
        assertEquals(unit, result.getUnit());
    }

    @Test
    void rejectsEndDateNotAfterStartDate() {
        assertThrows(IllegalArgumentException.class, () -> service.create(1L, "Piyush", start, start));
        assertThrows(IllegalArgumentException.class, () -> service.create(1L, "Piyush", end, start));
        verifyNoInteractions(units, bookings);
    }

    @Test
    void throwsNotFoundForUnknownUnit() {
        when(units.findByIdForUpdate(99L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.create(99L, "Piyush", start, end));
    }

    @Test
    void throwsConflictWhenOverlappingConfirmedBookingExists() {
        when(units.findByIdForUpdate(1L)).thenReturn(Optional.of(unit));
        when(bookings.existsByUnitIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
                1L, BookingStatus.CONFIRMED, end, start)).thenReturn(true);

        assertThrows(ConflictException.class, () -> service.create(1L, "Piyush", start, end));
        verify(bookings, never()).save(any());
    }

    @Test
    void cancelSetsStatusToCancelled() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CONFIRMED);
        when(bookings.findById(5L)).thenReturn(Optional.of(booking));
        when(bookings.save(booking)).thenReturn(booking);

        assertEquals(BookingStatus.CANCELLED, service.cancel(5L).getStatus());
    }

    @Test
    void cancellingTwiceIsAConflict() {
        Booking booking = new Booking();
        booking.setStatus(BookingStatus.CANCELLED);
        when(bookings.findById(5L)).thenReturn(Optional.of(booking));

        assertThrows(ConflictException.class, () -> service.cancel(5L));
        verify(bookings, never()).save(any());
    }

    @Test
    void getByIdThrowsWhenMissing() {
        when(bookings.findById(7L)).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, () -> service.getById(7L));
    }
}
