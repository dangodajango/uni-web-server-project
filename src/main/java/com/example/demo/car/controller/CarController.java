package com.example.demo.car.controller;

import com.example.demo.car.model.CreateCarDTO;
import com.example.demo.car.model.ResponseCarDTO;
import com.example.demo.car.model.UpdateCarDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping("/cars")
public class CarController {

    @GetMapping
    public List<ResponseCarDTO> readAll(
            @RequestParam(required = false) String carMake,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear
    ) {
        return List.of();
    }

    @GetMapping("{id}")
    public ResponseCarDTO readById(@PathVariable Integer id) {
        return new ResponseCarDTO(1, "make", "model", 0, "licensePlate", List.of());
    }

    @PostMapping
    public ResponseCarDTO create(@RequestBody CreateCarDTO createCarDTO) {
        return new ResponseCarDTO(1, "make", "model", 0, "licensePlate", List.of());
    }

    @PutMapping("{id}")
    public ResponseCarDTO update(@PathVariable Integer id, @RequestBody UpdateCarDTO createCarDTO) {
        return new ResponseCarDTO(1, "make", "model", 0, "licensePlate", List.of());
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable Integer id) {
        return true;
    }
}
