package com.example.demo.car.exception;

import com.example.demo.common.exception.ResourceNotFoundException;

public class CarNotFoundException extends ResourceNotFoundException {

    public CarNotFoundException(Integer id) {
        super("Missing car with ID - %s".formatted(id));
    }
}
