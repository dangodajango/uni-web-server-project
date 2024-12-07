package com.example.demo.garage.model;

public record CreateGarageDTO(
        String name,
        String location,
        String city,
        Integer capacity
) {
}
