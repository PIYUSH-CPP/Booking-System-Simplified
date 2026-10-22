package com.example.bookingsystem.booking;

import jakarta.validation.constraints.FutureOrPresent;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;

public record CreateBookingRequest(
        @NotNull Long unitId,
        @NotBlank String customerName,
        @NotNull @FutureOrPresent LocalDate startDate,
        @NotNull LocalDate endDate) {}
