package com.example.dormmatching.entity.record;

import com.example.dormmatching.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "health_discipline")
public class HealthDiscipline {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "has_infectious_disease")
    private Boolean hasInfectiousDisease;

    @Column(name = "forced_expulsion")
    private Boolean forcedExpulsion;

    @Column(name = "academic_discipline")
    private Boolean academicDiscipline;

    @Column(name = "penalty_points")
    private Integer penaltyPoints;

    // getters and setters...
}