package com.example.bookingsystem.booking;

import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDate;
import java.util.List;

public interface BookingRepository extends JpaRepository<Booking, Long> {
    List<Booking> findByCustomerNameIgnoreCase(String customerName);
    boolean existsByUnitIdAndStatusAndStartDateLessThanAndEndDateGreaterThan(
            Long unitId, BookingStatus status, LocalDate requestedEnd, LocalDate requestedStart);
}
