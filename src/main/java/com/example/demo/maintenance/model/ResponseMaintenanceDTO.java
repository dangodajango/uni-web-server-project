package com.example.demo.maintenance.model;

import java.time.LocalDate;

public record ResponseMaintenanceDTO(
        Integer id,
        Integer carId,
        String carName,
        String serviceType,
        LocalDate scheduledDate,
        Integer garageId,
        String garageName
) {
}
