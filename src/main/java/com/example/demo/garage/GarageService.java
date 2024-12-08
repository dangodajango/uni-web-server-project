package com.example.demo.garage;

import com.example.demo.garage.model.CreateGarageDTO;
import com.example.demo.garage.model.DailyAvailabilityReportDTO;
import com.example.demo.garage.model.ResponseGarageDTO;
import com.example.demo.garage.model.UpdateGarageDTO;
import com.example.demo.garage.persistance.Garage;
import com.example.demo.garage.persistance.GarageRepository;
import jakarta.annotation.Nullable;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

import static com.example.demo.util.FieldUtils.updateFieldIfNotNull;
import static java.util.Objects.isNull;

@Service
@RequiredArgsConstructor
public class GarageService {

    private final GarageRepository garageRepository;

    public List<ResponseGarageDTO> readAll(@Nullable String city) {
        List<Garage> garages = isNull(city) ? garageRepository.findAll() : garageRepository.findByCity(city);
        return garages.stream()
                .map(garage -> new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity()))
                .toList();
    }

    public ResponseGarageDTO readById(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    public List<DailyAvailabilityReportDTO> getDailyAvailabilityReport(Integer garageId, String startDate, String endDate) {
        return null;
    }

    public ResponseGarageDTO create(CreateGarageDTO dto) {
        Garage garage = Garage.builder()
                .name(dto.name())
                .location(dto.location())
                .city(dto.city())
                .capacity(dto.capacity())
                .build();
        garageRepository.save(garage);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    public ResponseGarageDTO update(Integer id, UpdateGarageDTO dto) {
        Garage garage = garageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        updateFieldIfNotNull(dto::name, garage::setName);
        updateFieldIfNotNull(dto::location, garage::setLocation);
        updateFieldIfNotNull(dto::city, garage::setCity);
        updateFieldIfNotNull(dto::capacity, garage::setCapacity);
        return new ResponseGarageDTO(garage.getId(), garage.getName(), garage.getLocation(), garage.getCity(), garage.getCapacity());
    }

    public boolean delete(Integer id) {
        Garage garage = garageRepository.findById(id).orElseThrow(IllegalArgumentException::new);
        try {
            garageRepository.delete(garage);
            return true;
        } catch (Exception exception) {
            return false;
        }
    }
}
