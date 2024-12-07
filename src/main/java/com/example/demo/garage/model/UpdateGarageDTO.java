package com.example.demo.garage.model;

public record UpdateGarageDTO(
        String name,
        String location,
        String city,
        Integer capacity
) {
}
