package com.example.dormmatching.entity.application;

import com.fasterxml.jackson.annotation.JsonManagedReference;  // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "application_period")
public class ApplicationPeriod {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "period_id")
    private Integer periodId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "end_date", nullable = false)
    private LocalDate endDate;

    // ← 여기에 @JsonManagedReference 추가
    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<DormCapacity> capacities = new ArrayList<>();

    @OneToMany(mappedBy = "period", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<DormApplication> applications = new ArrayList<>();
}
