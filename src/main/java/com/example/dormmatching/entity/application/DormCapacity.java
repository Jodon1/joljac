package com.example.dormmatching.entity.application;

import com.fasterxml.jackson.annotation.JsonBackReference;  // 추가
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "dorm_capacity")
public class DormCapacity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "capacity_id")
    private Long capacityId;

    @Column(name = "gender", nullable = false, length = 1)
    private String gender; // 'M' 또는 'F'

    @Column(name = "capacity", nullable = false)
    private Integer capacity;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    @JsonBackReference  // ← 여기에 @JsonBackReference 추가
    private ApplicationPeriod period;
}
