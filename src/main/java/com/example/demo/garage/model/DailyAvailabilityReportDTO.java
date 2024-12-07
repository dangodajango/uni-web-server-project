package com.example.demo.garage.model;

import java.time.LocalDate;

public record DailyAvailabilityReportDTO(
        LocalDate date,
        Integer requests,
        Integer availableCapacity
) {
}
