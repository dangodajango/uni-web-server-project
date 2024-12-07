package com.example.demo.maintenance.model;

import java.time.LocalDate;

public record CreateMaintenanceDTO(
        Integer carId,
        String serviceType,
        LocalDate scheduledDate,
        Integer garageId
) {
}
