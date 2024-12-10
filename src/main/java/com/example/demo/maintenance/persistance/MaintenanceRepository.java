package com.example.demo.maintenance.persistance;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface MaintenanceRepository extends JpaRepository<Maintenance, Integer> {

    List<Maintenance> findByGarageId(Integer garageId);
}
