package com.example.demo.maintenance.model;

import java.time.LocalDate;

public record UpdateMaintenanceDTO(
        Integer carId,
        String serviceType,
        LocalDate scheduledDate,
        Integer garageId
) {
}
