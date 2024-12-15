package com.example.demo.maintenance.model;

public record MonthlyRequestsReportDTO(
        YearMonthDTO yearMonth,
        Integer requests
) {
}
