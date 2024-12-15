package com.example.demo.maintenance.model;

public record YearMonthDTO(
        Integer year,
        String month,
        Boolean leapYear,
        Integer monthValue
) {
}
