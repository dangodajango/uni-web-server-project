package com.example.demo.garage.persistance;

import com.example.demo.car.persistance.Car;
import jakarta.persistence.*;
import lombok.*;

import java.util.Set;

@Entity
@Table(name = "garage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Garage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;

    private String location;

    private String city;

    private Integer capacity;

    @ManyToMany(mappedBy = "garages")
    private Set<Car> cars;
}
