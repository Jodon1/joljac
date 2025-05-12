package com.example.dormmatching.entity.application;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "application_period")
public class ApplicationPeriod {
    @Id
    @Column(name = "period_id")
    private Integer periodId;

    @Column(length = 100)
    private String name;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @OneToMany(mappedBy = "period")
    private Set<DormApplication> applications;

    @OneToMany(mappedBy = "period")
    private Set<SelectionResult> results;

    // getters and setters...
}