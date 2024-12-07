package com.example.demo.car;

import com.example.demo.car.model.CreateCarDTO;
import com.example.demo.car.model.ResponseCarDTO;
import com.example.demo.car.model.UpdateCarDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cars")
@RequiredArgsConstructor
public class CarController {

    private final CarService carService;

    @GetMapping
    public List<ResponseCarDTO> readAll(
            @RequestParam(required = false) String carMake,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) Integer fromYear,
            @RequestParam(required = false) Integer toYear
    ) {
        return carService.readAll(carMake, garageId, fromYear, toYear);
    }

    @GetMapping("/{id}")
    public ResponseCarDTO readById(@PathVariable Integer id) {
        return carService.readById(id);
    }

    @PostMapping
    public ResponseCarDTO create(@RequestBody CreateCarDTO createCarDTO) {
        return carService.create(createCarDTO);
    }

    @PutMapping("/{id}")
    public ResponseCarDTO update(@PathVariable Integer id, @RequestBody UpdateCarDTO updateCarDTO) {
        return carService.update(id, updateCarDTO);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return carService.delete(id);
    }
}
