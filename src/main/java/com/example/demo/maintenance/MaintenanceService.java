package com.example.demo.maintenance;

import com.example.demo.maintenance.model.CreateMaintenanceDTO;
import com.example.demo.maintenance.model.MonthlyRequestsReportDTO;
import com.example.demo.maintenance.model.ResponseMaintenanceDTO;
import com.example.demo.maintenance.model.UpdateMaintenanceDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MaintenanceService {

    public List<ResponseMaintenanceDTO> readAll(Integer carId, Integer garageId, String startDate, Integer endDate) {
        return null;
    }

    public ResponseMaintenanceDTO readById(Integer id) {
        return null;
    }

    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(Integer garageId, String startMonth, String endMonth) {
        return null;
    }

    public ResponseMaintenanceDTO create(CreateMaintenanceDTO createMaintenanceDTO) {
        return null;
    }

    public ResponseMaintenanceDTO update(Integer id, UpdateMaintenanceDTO updateMaintenanceDTO) {
        return null;
    }

    public boolean delete(Integer id) {
        return false;
    }
}
