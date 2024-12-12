package com.example.demo.maintenance.model;

public record YearMonth(
        Integer year,
        String month,
        Boolean leapYear,
        Integer monthValue
) {
}
