package com.example.demo.maintenance.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    List<Maintenance> findByGarageId(Integer garageId);

    List<Maintenance> findByGarageIdAndScheduledDate(Integer garageId, LocalDate scheduledDate);

    List<Maintenance> findByGarageIdAndCarId(Integer garageId, Integer carId);
}
