package com.example.demo.garage.persistance;

import jakarta.persistence.*;
import lombok.*;

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
}
