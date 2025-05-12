package com.example.dormmatching.entity.support;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "departments")
@Getter @Setter
public class Department {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "department_id")
    private Integer departmentId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    @Column(name = "is_first_year_only", nullable = false)
    private boolean isFirstYearOnly = false;

    // 단과대학과의 관계 (Many Departments → One College)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "college_id", nullable = false)
    private College college;
}