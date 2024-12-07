package com.example.demo.car;

import com.example.demo.car.model.CreateCarDTO;
import com.example.demo.car.model.ResponseCarDTO;
import com.example.demo.car.model.UpdateCarDTO;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CarService {

    public List<ResponseCarDTO> readAll(String carMake, Integer garageId, Integer fromYear, Integer toYear) {
        return null;
    }

    public ResponseCarDTO readById(Integer id) {
        return null;
    }

    public ResponseCarDTO create(CreateCarDTO createCarDTO) {
        return null;
    }

    public ResponseCarDTO update(Integer id, UpdateCarDTO createCarDTO) {
        return null;
    }

    public boolean delete(Integer id) {
        return false;
    }
}
