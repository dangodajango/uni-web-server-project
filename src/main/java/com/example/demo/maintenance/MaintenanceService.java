package com.example.demo.maintenance;

import com.example.demo.car.exception.CarNotFoundException;
import com.example.demo.car.persistance.Car;
import com.example.demo.car.persistance.CarRepository;
import com.example.demo.common.exception.RequestValidationException;
import com.example.demo.garage.exception.GarageNotFoundException;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import com.example.demo.maintenance.exception.MaintenanceNotFoundException;
import com.example.demo.maintenance.model.*;
import com.example.demo.maintenance.persistance.Maintenance;
import com.example.demo.maintenance.persistance.MaintenanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
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
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));
        return mapToResponseMaintenanceDTO(maintenance);
    }

    public List<MonthlyRequestsReportDTO> getMonthlyRequestsReport(Integer garageId, YearMonth startMonth, YearMonth endMonth) {
        Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate = maintenanceRepository.findByGarageId(garageId).stream()
                .filter(maintenance -> isScheduledDateInRange(maintenance.getScheduledDate(), startMonth, endMonth))
                .collect(groupingBy(maintenance -> YearMonth.from(maintenance.getScheduledDate())));
        return produceMonthlyRequestsReport(maintenancesGroupedByScheduledDate, startMonth, endMonth);
    }

    private boolean isScheduledDateInRange(LocalDate scheduledDate, YearMonth startMonth, YearMonth endMonth) {
        YearMonth scheduledDateInYearMonthFormat = YearMonth.from(scheduledDate);
        return startMonth.isBefore(scheduledDateInYearMonthFormat) && endMonth.isAfter(scheduledDateInYearMonthFormat);
    }

    private List<MonthlyRequestsReportDTO> produceMonthlyRequestsReport(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth startMonth, YearMonth endMonth) {
        List<MonthlyRequestsReportDTO> result = new ArrayList<>();
        YearMonth currentMonth = startMonth;
        while (currentMonth.isBefore(endMonth)) {
            if (maintenanceForGivenYearMonthExist(maintenancesGroupedByScheduledDate, currentMonth)) {
                result.add(createReportWithRequests(maintenancesGroupedByScheduledDate, currentMonth));
            } else {
                result.add(createReportWithoutRequests(currentMonth));
            }
            currentMonth = currentMonth.plusMonths(1);
        }
        return result;
    }

    private boolean maintenanceForGivenYearMonthExist(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth yearMonth) {
        return maintenancesGroupedByScheduledDate.containsKey(yearMonth);
    }

    private MonthlyRequestsReportDTO createReportWithRequests(Map<YearMonth, List<Maintenance>> maintenancesGroupedByScheduledDate, YearMonth currentMonth) {
        List<Maintenance> maintenancesForCurrentMonth = maintenancesGroupedByScheduledDate.get(currentMonth);
        Integer numberOfRequests = maintenancesForCurrentMonth.size();
        YearMonthDTO yearMonthDTO = mapToYearMonthDTO(currentMonth);
        return new MonthlyRequestsReportDTO(yearMonthDTO, numberOfRequests);
    }

    private MonthlyRequestsReportDTO createReportWithoutRequests(YearMonth currentMonth) {
        YearMonthDTO yearMonthDTO = mapToYearMonthDTO(currentMonth);
        int numberOfRequests = 0;
        return new MonthlyRequestsReportDTO(yearMonthDTO, numberOfRequests);
    }

    private YearMonthDTO mapToYearMonthDTO(YearMonth yearMonth) {
        Integer year = yearMonth.getYear();
        String month = yearMonth.getMonth().toString();
        Boolean isLeapYear = yearMonth.isLeapYear();
        Integer monthValue = yearMonth.getMonthValue();
        return new YearMonthDTO(year, month, isLeapYear, monthValue);
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
        Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));

        Garage garage = dto.garageId() != null ? garageRepository.findById(dto.garageId()).orElseThrow(() -> new GarageNotFoundException(dto.garageId())) : maintenance.getGarage();
        Car car = dto.carId() != null ? carRepository.findById(dto.carId()).orElseThrow(() -> new CarNotFoundException(dto.carId())) : maintenance.getCar();
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
            throw new RequestValidationException("garage with ID %s does not have capacity for date %s".formatted(garage.getId(), scheduledDate));
        }
    }

    private void verifyCarIsRegisteredInSpecificGarage(Car car, Garage garage) {
        if (!garage.getCars().contains(car)) {
            throw new RequestValidationException("car with ID %s it not registered in the garage with ID %s".formatted(car.getId(), garage.getId()));
        }
    }

    @Transactional
    public boolean delete(Integer id) {
        try {
            Maintenance maintenance = maintenanceRepository.findById(id).orElseThrow(() -> new MaintenanceNotFoundException(id));
            maintenance.setGarage(null);
            maintenance.setCar(null);
            maintenanceRepository.delete(maintenance);
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
