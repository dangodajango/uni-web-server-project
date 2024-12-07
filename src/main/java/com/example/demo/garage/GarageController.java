package com.example.demo.garage;

import com.example.demo.garage.model.CreateGarageDTO;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.model.UpdateGarageDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/garages")
public class GarageController {

    @GetMapping
    public List<ResponseGarageDTO> readAll(@RequestParam(required = false) String city) {
        return null;
    }

    @GetMapping("{id}")
    public ResponseGarageDTO readById(@PathVariable Integer id) {
        return null;
    }

    @PostMapping
    public ResponseGarageDTO create(@RequestBody CreateGarageDTO createGarageDTO) {
        return null;
    }

    @PutMapping("{id}")
    public ResponseGarageDTO update(@PathVariable Integer id, @RequestBody UpdateGarageDTO updateGarageDTO) {
        return null;
    }

    @DeleteMapping("{id}")
    public boolean delete(@PathVariable Integer id) {
        return false;
    }
}
