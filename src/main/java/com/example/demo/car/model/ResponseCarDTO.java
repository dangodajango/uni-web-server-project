package com.example.demo.car.model;

import com.example.demo.garage.model.ResponseGarageDTO;

import java.util.List;

public record ResponseCarDTO(
        Integer id,
        String make,
        String model,
        Integer productionYear,
        String licensePlate,
        List<ResponseGarageDTO> garages
) {
}
