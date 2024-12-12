package com.example.demo.car;

import com.example.demo.car.model.CreateCarDTO;
import com.example.demo.car.model.ResponseCarDTO;
import com.example.demo.car.model.UpdateCarDTO;
import com.example.demo.car.persistance.Car;
import com.example.demo.car.persistance.CarRepository;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.example.demo.util.FieldUtils.updateFieldIfNotNull;

@Service
@RequiredArgsConstructor
public class CarService {

    private final CarRepository carRepository;

    private final GarageRepository garageRepository;

    public List<ResponseCarDTO> readAll(String carMake, Integer garageId, Integer fromYear, Integer toYear) {
        return carRepository.findByMakeAndGaragesId(carMake, garageId).stream()
                .filter(car -> isProductionYearInRange(car.getProductionYear(), fromYear, toYear))
                .map(this::mapToResponseCarDTO)
                .toList();
    }

    private boolean isProductionYearInRange(Integer productionYear, Integer fromYear, Integer toYear) {
        boolean isAfterFromYear = true;
        if (fromYear != null) {
            isAfterFromYear = productionYear >= fromYear;
        }
        boolean isBeforeToYear = true;
        if (toYear != null) {
            isBeforeToYear = productionYear <= toYear;
        }
        return isAfterFromYear && isBeforeToYear;
    }

    public ResponseCarDTO readById(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return mapToResponseCarDTO(car);
    }

    @Transactional
    public ResponseCarDTO create(CreateCarDTO dto) {
        Set<Garage> garages = findGarages(dto.garageIds());
        Car car = Car.builder()
                .make(dto.make())
                .model(dto.model())
                .productionYear(dto.productionYear())
                .licensePlate(dto.licensePlate())
                .garages(garages)
                .build();
        carRepository.save(car);
        return mapToResponseCarDTO(car);
    }

    @Transactional
    public ResponseCarDTO update(Integer id, UpdateCarDTO dto) {
        Car car = carRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        updateFieldIfNotNull(dto::make, car::setMake);
        updateFieldIfNotNull(dto::model, car::setModel);
        updateFieldIfNotNull(dto::productionYear, car::setProductionYear);
        updateFieldIfNotNull(dto::licensePlate, car::setLicensePlate);
        updateFieldIfNotNull(dto::garageIds, garageIds -> car.setGarages(findGarages(dto.garageIds())));
        return mapToResponseCarDTO(car);
    }

    private Set<Garage> findGarages(List<Integer> garageIds) {
        List<Garage> garages = garageRepository.findAllById(garageIds);
        if (garages.size() != garageIds.size()) {
            throw new IllegalStateException();
        }
        return new HashSet<>(garages);
    }

    @Transactional
    public boolean delete(Integer id) {
        Car car = carRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        try {
            car.getGarages().forEach(garage -> garage.getCars().remove(car));
            carRepository.delete(car);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }

    private ResponseCarDTO mapToResponseCarDTO(Car car) {
        return new ResponseCarDTO(
                car.getId(),
                car.getMake(),
                car.getModel(),
                car.getProductionYear(),
                car.getLicensePlate(),
                car.getGarages()
                        .stream()
                        .map(garage -> new ResponseGarageDTO(
                                garage.getId(),
                                garage.getName(),
                                garage.getLocation(),
                                garage.getCity(),
                                garage.getCapacity()))
                        .toList()
        );
    }
}
