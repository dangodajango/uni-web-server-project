package com.example.demo.maintenance.model;

public record MonthlyRequestsReportDTO(
        YearMonth yearMonth,
        Integer requests
) {
}
