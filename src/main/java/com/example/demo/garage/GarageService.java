package com.example.demo.garage;

import com.example.demo.garage.model.CreateGarageDTO;
import com.example.demo.garage.model.DailyAvailabilityReportDTO;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.model.UpdateGarageDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class GarageService {

    public List<ResponseGarageDTO> readAll(String city) {
        return null;
    }

    public ResponseGarageDTO readById(Integer id) {
        return null;
    }

    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Integer garageId, String startDate, String endDate) {
        return null;
    }

    public ResponseGarageDTO create(CreateGarageDTO createGarageDTO) {
        return null;
    }

    public ResponseGarageDTO update(Integer id, UpdateGarageDTO updateGarageDTO) {
        return null;
    }

    public boolean delete(Integer id) {
        return false;
    }
}
