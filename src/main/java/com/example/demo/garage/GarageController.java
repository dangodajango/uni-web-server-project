package com.example.demo.garage;

import com.example.demo.garage.model.CreateGarageDTO;
import com.example.demo.garage.model.DailyAvailabilityReportDTO;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.model.UpdateGarageDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/garages")
@RequiredArgsConstructor
public class GarageController {

    private final GarageService garageService;

    @GetMapping
    public List<ResponseGarageDTO> readAll(@RequestParam(required = false) String city) {
        return garageService.readAll(city);
    }

    @GetMapping("/{id}")
    public ResponseGarageDTO readById(@PathVariable Integer id) {
        return garageService.readById(id);
    }

    @GetMapping("/dailyAvailabilityReport")
    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(
            @RequestParam Integer garageId,
            @RequestParam LocalDate startDate,
            @RequestParam LocalDate endDate) {
        return garageService.getDailyAvailabilityReport(garageId, startDate, endDate);
    }

    @PostMapping
    public ResponseGarageDTO create(@RequestBody CreateGarageDTO createGarageDTO) {
        return garageService.create(createGarageDTO);
    }

    @PutMapping("/{id}")
    public ResponseGarageDTO update(@PathVariable Integer id, @RequestBody UpdateGarageDTO updateGarageDTO) {
        return garageService.update(id, updateGarageDTO);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return garageService.delete(id);
    }
}
