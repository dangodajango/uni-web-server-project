package com.example.demo.garage;

import com.example.demo.garage.exception.GarageNotFoundException;
import com.example.demo.garage.model.CreateGarageDTO;
import com.example.demo.garage.model.DailyAvailabilityReportDTO;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.model.UpdateGarageDTO;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import com.example.demo.maintenance.persistance.Maintenance;
import com.example.demo.maintenance.persistance.MaintenanceRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.example.demo.common.util.FieldUtils.updateFieldIfNotNull;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageRepository garageRepository;

    private final MaintenanceRepository maintenanceRepository;

    public List<ResponseGarageDTO> readAll(@Nullable String city) {
        List<Garage> garages = isNull(city) ? garageRepository.findAll() : garageRepository.findByCity(city);
        return garages.stream()
                .map(garage -> new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity()))
                .toList();
    }

    public ResponseGarageDTO readById(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Integer garageId, LocalDate startDate, LocalDate endDate) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
        Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate = maintenanceRepository.findByGarageIdAndScheduledDateBetween(garageId, startDate, endDate).stream()
                .collect(Collectors.groupingBy(Maintenance::getScheduledDate));
        return produceDailyAvailabilityReportInRange(maintenancesGroupedByScheduledDate, startDate, endDate, garage);
    }

    private List<DailyAvailabilityReportDTO> produceDailyAvailabilityReportInRange(Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate, LocalDate startDate, LocalDate endDate, Garage garage) {
        List<DailyAvailabilityReportDTO> dailyAvailabilityReports = new ArrayList<>();
        LocalDate currentDate = startDate;
        while (!currentDate.isAfter(endDate)) {
            if (maintenancesForGivenDateExist(maintenancesGroupedByScheduledDate, currentDate)) {
                dailyAvailabilityReports.add(createReportWithRequests(garage, currentDate, maintenancesGroupedByScheduledDate));
            } else {
                dailyAvailabilityReports.add(createReportWithNoRequests(garage, currentDate));
            }
            currentDate = currentDate.plusDays(1);
        }
        return dailyAvailabilityReports;
    }

    private boolean maintenancesForGivenDateExist(Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate, LocalDate date) {
        return maintenancesGroupedByScheduledDate.containsKey(date);
    }

    private DailyAvailabilityReportDTO createReportWithRequests(Garage garage, LocalDate date, Map<LocalDate, List<Maintenance>> maintenancesGroupedByScheduledDate) {
        int maintenanceRequests = maintenancesGroupedByScheduledDate.get(date).size();
        int availableCapacity = garage.getCapacity();
        return new DailyAvailabilityReportDTO(date, maintenanceRequests, availableCapacity);
    }

    private DailyAvailabilityReportDTO createReportWithNoRequests(Garage garage, LocalDate date) {
        return new DailyAvailabilityReportDTO(date, 0, garage.getCapacity());
    }

    @Transactional
    public ResponseGarageDTO create(CreateGarageDTO dto) {
        Garage garage = Garage.builder()
                .name(dto.name())
                .location(dto.location())
                .city(dto.City())
                .capacity(dto.capacity())
                .build();
        garageRepository.save(garage);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public ResponseGarageDTO update(Integer id, UpdateGarageDTO dto) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        updateFieldIfNotNull(dto::name, garage::setName);
        updateFieldIfNotNull(dto::location, garage::setLocation);
        updateFieldIfNotNull(dto::city, garage::setCity);
        updateFieldIfNotNull(dto::capacity, garage::setCapacity);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    @Transactional
    public boolean delete(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(() -> new GarageNotFoundException(id));
        try {
            garage.getCars().forEach(car -> car.getGarages().remove(garage));
            garageRepository.delete(garage);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
