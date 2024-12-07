package com.example.demo.maintenance;

import com.example.demo.maintenance.model.CreateMaintenanceDTO;
import com.example.demo.maintenance.model.MonthlyRequestsReportDTO;
import com.example.demo.maintenance.model.ResponseMaintenanceDTO;
import com.example.demo.maintenance.model.UpdateMaintenanceDTO;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/maintenance")
public class MaintenanceController {

    @GetMapping
    public List<ResponseMaintenanceDTO> readAll() {
        return null;
    }

    @GetMapping("/{id}")
    public ResponseMaintenanceDTO readById(@PathVariable Integer id) {
        return null;
    }

    @GetMapping("/monthlyRequestsReport")
    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(
            @RequestParam Integer garageId,
            @RequestParam String startMonth,
            @RequestParam String endMonth) {
        return null;
    }

    @PostMapping
    public ResponseMaintenanceDTO create(@RequestBody CreateMaintenanceDTO createMaintenanceDTO) {
        return null;
    }

    @PutMapping("/{id}")
    public ResponseMaintenanceDTO update(@PathVariable Integer id, @RequestBody UpdateMaintenanceDTO updateMaintenanceDTO) {
        return null;
    }

    @DeleteMapping("/{id}")
    public boolean delete(@PathVariable Integer id) {
        return false;
    }
}
