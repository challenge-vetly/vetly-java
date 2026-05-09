package com.petly.petly_java.model;

import jakarta.persistence.*;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.UUID;

@Entity
public class Disponibilidade {

    @Id
    @GeneratedValue
    private UUID id;

    @Enumerated(EnumType.STRING)
    private DayOfWeek diaSemana;

    private LocalTime inicio;

    private LocalTime fim;
}