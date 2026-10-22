package com.example.bookingsystem.booking;

import com.example.bookingsystem.unit.Unit;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDate;

@Entity
@Table(name = "bookings", indexes = {
        // Supports the overlap query: unit + status, then date range comparisons
        @Index(name = "idx_booking_unit_status_dates", columnList = "unit_id, status, start_date, end_date"),
        @Index(name = "idx_booking_customer_name", columnList = "customer_name")
})
public class Booking {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.EAGER)
    @JoinColumn(name = "unit_id", nullable = false)
    private Unit unit;

    @NotBlank
    @Column(name = "customer_name", nullable = false)
    private String customerName;

    // NOTE: @FutureOrPresent is intentionally NOT on the entity. Hibernate re-validates entities on every
    // update, so cancelling a booking whose start date has already passed would fail. The "not in the past"
    // rule applies only to NEW bookings and is enforced on CreateBookingRequest.
    @NotNull
    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @NotNull
    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookingStatus status = BookingStatus.CONFIRMED;

    public Booking() {}
    public Long getId() { return id; }
    public Unit getUnit() { return unit; }
    public String getCustomerName() { return customerName; }
    public LocalDate getStartDate() { return startDate; }
    public LocalDate getEndDate() { return endDate; }
    public BookingStatus getStatus() { return status; }
    public void setId(Long id) { this.id = id; }
    public void setUnit(Unit unit) { this.unit = unit; }
    public void setCustomerName(String customerName) { this.customerName = customerName; }
    public void setStartDate(LocalDate startDate) { this.startDate = startDate; }
    public void setEndDate(LocalDate endDate) { this.endDate = endDate; }
    public void setStatus(BookingStatus status) { this.status = status; }
}
