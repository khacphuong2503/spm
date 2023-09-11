package com.javatechie.entity;

import lombok.Data;

import jakarta.persistence.*;

@Entity
@Data
public class MaintenanceStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private boolean status;

}
