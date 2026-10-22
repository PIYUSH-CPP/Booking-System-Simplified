package com.example.bookingsystem.booking;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {
    private final BookingService service;
    public BookingController(BookingService service) { this.service = service; }

    @GetMapping public List<Booking> getAll(@RequestParam(required = false) String customerName) { return service.getAll(customerName); }
    @GetMapping("/{id}") public Booking getById(@PathVariable Long id) { return service.getById(id); }
    @PostMapping @ResponseStatus(HttpStatus.CREATED)
    public Booking create(@Valid @RequestBody CreateBookingRequest request) {
        return service.create(request.unitId(), request.customerName(), request.startDate(), request.endDate());
    }
    @PatchMapping("/{id}/cancel") public Booking cancel(@PathVariable Long id) { return service.cancel(id); }
}
