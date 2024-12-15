package com.example.demo.maintenance;

import com.example.demo.maintenance.model.CreateMaintenanceDTO;
import com.example.demo.maintenance.model.MonthlyRequestsReportDTO;
import com.example.demo.maintenance.model.ResponseMaintenanceDTO;
import com.example.demo.maintenance.model.UpdateMaintenanceDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;

@RestController
@RequestMapping("/maintenance")
@RequiredArgsConstructor
public class MaintenanceController {

    private final MaintenanceService maintenanceService;

    @GetMapping
    public List<ResponseMaintenanceDTO> readAll(
            @RequestParam(required = false) Integer carId,
            @RequestParam(required = false) Integer garageId,
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate
    ) {
        return maintenanceService.readAll(carId, garageId, startDate, endDate);
    }

    @GetMapping("/{id}")
    public ResponseMaintenanceDTO readById(@PathVariable Integer id) {
        return maintenanceService.readById(id);
    }

    @GetMapping("/monthlyRequestsReport")
    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(
            @RequestParam Integer garageId,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth startMonth,
            @RequestParam @DateTimeFormat(pattern = "yyyy-MM") YearMonth endMonth) {
        return maintenanceService.getMonthlyRequestsReport(garageId, startMonth, endMonth);
    }

    @PostMapping
    public ResponseMaintenanceDTO create(@RequestBody CreateMaintenanceDTO createMaintenanceDTO) {
        return maintenanceService.create(createMaintenanceDTO);
    }

    @PutMapping("/{id}")
    public ResponseMaintenanceDTO update(@PathVariable Integer id, @RequestBody UpdateMaintenanceDTO updateMaintenanceDTO) {
        return maintenanceService.update(id, updateMaintenanceDTO);
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return maintenanceService.delete(id);
    }
}
