package com.example.demo.garage.model;

public record ResponseGarageDTO(
        Integer id,
        String name,
        String location,
        String city,
        Integer capacity
) {
}
