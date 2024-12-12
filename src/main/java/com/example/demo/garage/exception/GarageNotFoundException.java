package com.example.demo.garage.exception;

import com.example.demo.common.exception.ResourceNotFoundException;

import java.util.List;

public class GarageNotFoundException extends ResourceNotFoundException {

    public GarageNotFoundException(Integer id) {
        super("Missing garage with ID - %s".formatted(id));
    }

    public GarageNotFoundException(List<Integer> ids) {
        super("Missing garages with IDs - %s".formatted(ids));
    }
}
