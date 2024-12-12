package com.example.demo.maintenance;

import com.example.demo.car.persistance.Car;
import com.example.demo.car.persistance.CarRepository;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import com.example.demo.maintenance.model.*;
import com.example.demo.maintenance.persistance.Maintenance;
import com.example.demo.maintenance.persistance.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Map;

import static java.util.stream.Collectors.groupingBy;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<ResponseMaintenanceDTO> readAll(Integer carId, Integer garageId, LocalDate startDate, LocalDate endDate) {
        return maintenanceRepository.findByGarageIdAndCarId(garageId, carId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startDate, endDate))
                .map(this::mapToResponseMaintenanceDTO)
                .toList();
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, LocalDate startDate, LocalDate endDate) {
        boolean isAfterStartDate = true;
        if (startDate != null) {
            isAfterStartDate = scheduledDate.isAfter(startDate);
        }
        boolean isBeforeEndDate = true;
        if (endDate != null) {
            isBeforeEndDate = scheduledDate.isBefore(endDate);
        }
        return isAfterStartDate && isBeforeEndDate;
    }

    public ResponseMaintenanceDTO readById(Integer id) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return mapToResponseMaintenanceDTO(maintenance);
    }

    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(Integer garageId, String startMonth, String endMonth) {
        Map<YearMonth, List<Maintenance>> maintenancesGroupedByYearAndMonth = maintenanceRepository.findByGarageId(garageId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startMonth, endMonth))
                .collect(groupingBy(maintenance -> {
                    LocalDate scheduledDate = maintenance.getScheduledDate();
                    return new YearMonth(scheduledDate.getYear(), scheduledDate.getMonth().toString(), scheduledDate.isLeapYear(), scheduledDate.getMonthValue());
                }));
        return maintenancesGroupedByYearAndMonth.entrySet().stream()
                .map(entry -> {
                    YearMonth yearMonth = entry.getKey();
                    List<Maintenance> maintenancesPerformedInYearMonth = entry.getValue();
                    return new MonthlyRequestsReportDTO(yearMonth, maintenancesPerformedInYearMonth.size());
                }).toList();
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, String startMonth, String endMonth) {
        int scheduledDateMonth = scheduledDate.getMonthValue();
        int startMonthValue = Month.valueOf(startMonth).getValue();
        int endMonthValue = Month.valueOf(endMonth).getValue();
        return scheduledDateMonth >= startMonthValue && scheduledDateMonth <= endMonthValue;
    }

    @Transactional
    public ResponseMaintenanceDTO create(CreateMaintenanceDTO dto) {
        Garage garage = garageRepository.findById(dto.garageId()).orElseThrow(IllegalArgumentException::new);
        Car car = carRepository.findById(dto.carId()).orElseThrow(IllegalArgumentException::new);

        verifyGarageHasEnoughCapacity(garage, dto.scheduledDate());
        verifyCarIsRegisteredInSpecificGarage(car, garage);

        Maintenance maintenance = Maintenance.builder()
                .car(car)
                .garage(garage)
                .serviceType(dto.serviceType())
                .scheduledDate(dto.scheduledDate())
                .build();
        maintenanceRepository.save(maintenance);
        return mapToResponseMaintenanceDTO(maintenance);
    }

    @Transactional
    public ResponseMaintenanceDTO update(Integer id, UpdateMaintenanceDTO dto) {
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(IllegalArgumentException::new);

        Garage garage = dto.garageId() != null ? garageRepository.findById(dto.garageId()).orElseThrow(IllegalArgumentException::new) : maintenance.getGarage();
        Car car = dto.carId() != null ? carRepository.findById(dto.carId()).orElseThrow(IllegalArgumentException::new) : maintenance.getCar();
        LocalDate scheduledDate = dto.scheduledDate() != null ? dto.scheduledDate() : maintenance.getScheduledDate();
        String serviceType = dto.serviceType() != null ? dto.serviceType() : maintenance.getServiceType();

        verifyGarageHasEnoughCapacity(garage, scheduledDate);
        verifyCarIsRegisteredInSpecificGarage(car, garage);

        updateMaintenanceEntity(maintenance, garage, car, scheduledDate, serviceType);
        return mapToResponseMaintenanceDTO(maintenance);
    }

    private void updateMaintenanceEntity(Maintenance maintenance, Garage garage, Car car, LocalDate scheduledDate, String serviceType) {
        maintenance.setGarage(garage);
        maintenance.setCar(car);
        maintenance.setScheduledDate(scheduledDate);
        maintenance.setServiceType(serviceType);
    }

    private void verifyGarageHasEnoughCapacity(Garage garage, LocalDate scheduledDate) {
        List<Maintenance> maintenances = maintenanceRepository.findByGarageIdAndScheduledDate(garage.getId(), scheduledDate);
        if (garage.getCapacity() <= maintenances.size()) {
            throw new IllegalStateException();
        }
    }

    private void verifyCarIsRegisteredInSpecificGarage(Car car, Garage garage) {
        if (!garage.getCars().contains(car)) {
            throw new IllegalStateException();
        }
    }

    @Transactional
    public boolean delete(Integer id) {
        try {
            maintenanceRepository.deleteById(id);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private ResponseMaintenanceDTO mapToResponseMaintenanceDTO(Maintenance maintenance) {
        return new ResponseMaintenanceDTO(maintenance.getId(),
                maintenance.getCar().getId(),
                constructCarName(maintenance.getCar()),
                maintenance.getServiceType(),
                maintenance.getScheduledDate(),
                maintenance.getGarage().getId(),
                maintenance.getGarage().getName());
    }

    private String constructCarName(Car car) {
        return car.getMake() + car.getModel() + car.getLicensePlate();
    }
}
