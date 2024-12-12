package com.example.demo.maintenance.exception;

import com.example.demo.common.exception.ResourceNotFoundException;

public class MaintenanceNotFoundException extends ResourceNotFoundException {

    public MaintenanceNotFoundException(Integer id) {
        super("Missing maintenance with ID - %s".formatted(id));
    }
}
