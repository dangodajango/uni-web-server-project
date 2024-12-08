package com.example.demo.car.persistance;

import com.example.demo.garage.persistance.Garage;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "car")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String make;

    private String model;

    @Column(name = "production_year")
    private Integer productionYear;

    @Column(name = "license_palte")
    private String licensePlate;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "car_garages",
            joinColumns = @JoinColumn(name = "car_id"),
            inverseJoinColumns = @JoinColumn(name = "garage_id")
    )
    private Set<Garage> garages = new HashSet<>();
}
