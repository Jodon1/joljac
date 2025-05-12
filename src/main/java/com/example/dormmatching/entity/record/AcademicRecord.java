package com.example.dormmatching.entity.record;

import com.example.dormmatching.entity.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@Table(name = "academic_record")
public class AcademicRecord {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "previous_semester_gpa")
    private Double previousSemesterGpa;

    @Column(name = "admission_score")
    private Integer admissionScore;

    @Column(name = "residency_semesters")
    private Integer residencySemesters;

    // getters and setters...
}