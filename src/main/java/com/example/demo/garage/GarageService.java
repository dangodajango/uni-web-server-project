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

    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Integer garageId, LocalDate startDate, LocalDate endDate) {
        Garage garage = garageRepository.findById(garageId).orElseThrow(() -> new GarageNotFoundException(garageId));
        Map<LocalDate, List<Maintenance>> maintenances = maintenanceRepository.findByGarageId(garageId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startDate, endDate))
                .collect(Collectors.groupingBy(Maintenance::getScheduledDate));
        return maintenances.entrySet().stream()
                .map(entry -> {
                    LocalDate date = entry.getKey();
                    int maintenanceRequests = entry.getValue().size();
                    int availableCapacity = garage.getCapacity();
                    return new DailyAvailabilityReportDTO(date, maintenanceRequests, availableCapacity);
                }).toList();
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, LocalDate startDate, LocalDate endDate) {
        return scheduledDate.isAfter(startDate) && scheduledDate.isBefore(endDate);
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
