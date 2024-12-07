package com.example.demo.car.model;

import java.util.List;

public record UpdateCarDTO(String make, String model, Integer productionYear,
                           String licensePlate, List<Integer> garageIds) {
}
