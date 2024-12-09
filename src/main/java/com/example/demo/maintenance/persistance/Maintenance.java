package com.example.demo.maintenance.persistance;


import com.example.demo.car.persistance.Car;
import com.example.demo.garage.persistance.Garage;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "maintenance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Maintenance {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "car_id", nullable = false)
    private Car car;

    @ManyToOne
    @JoinColumn(name = "garage_id", nullable = false)
    private Garage garage;

    @Column(name = "service_type")
    private String serviceType;

    @Column(name = "scheduled_date")
    private LocalDate scheduledDate;
}
