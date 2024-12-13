package com.example.demo.car.persistance;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CarRepository extends JpaRepository<Car, Integer> {

    @Query("""
            SELECT c FROM Car c JOIN c.garages g
            WHERE (:make IS NULL OR c.make = :make)
            AND (:garageId IS NULL OR g.id = :garageId)
            """)
    List<Car> findByMakeAndGaragesId(String make, Integer garageId);
}
