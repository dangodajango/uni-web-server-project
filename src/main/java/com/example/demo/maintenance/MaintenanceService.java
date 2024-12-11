package com.example.demo.maintenance;

import com.example.demo.car.persistance.Car;
import com.example.demo.car.persistance.CarRepository;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import com.example.demo.maintenance.model.CreateMaintenanceDTO;
import com.example.demo.maintenance.model.MonthlyRequestsReportDTO;
import com.example.demo.maintenance.model.ResponseMaintenanceDTO;
import com.example.demo.maintenance.model.UpdateMaintenanceDTO;
import com.example.demo.maintenance.persistance.Maintenance;
import com.example.demo.maintenance.persistance.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class MaintenanceService {

    private final MaintenanceRepository maintenanceRepository;

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<ResponseMaintenanceDTO> readAll(Integer carId, Integer garageId, LocalDate startDate, LocalDate endDate) {
        Maintenance probe = prepareMaintenanceProbe(carId, garageId);
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<Maintenance> exampleMaintenance = Example.of(probe, matcher);
        return maintenanceRepository.findAll(exampleMaintenance).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startDate, endDate))
                .map(this::mapToResponseMaintenanceDTO)
                .toList();
    }

    private Maintenance prepareMaintenanceProbe(Integer carId, Integer garageId) {
        Maintenance probe = new Maintenance();
        if (!isNull(carId)) {
            Car car = new Car();
            car.setId(carId);
            probe.setCar(car);
        }
        if (!isNull(garageId)) {
            Garage garage = new Garage();
            garage.setId(garageId);
            probe.setGarage(garage);
        }
        return probe;
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
        return null;
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

    public void verifyGarageHasEnoughCapacity(Garage garage, LocalDate scheduledDate) {
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
