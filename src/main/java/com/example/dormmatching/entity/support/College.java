package com.example.dormmatching.entity.support;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Entity
@Table(name = "colleges")
@Getter @Setter
public class College {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "college_id")
    private Integer collegeId;

    @Column(name = "name", nullable = false, length = 100)
    private String name;

    // 양방향이 필요하다면
    @OneToMany(mappedBy = "college", fetch = FetchType.LAZY)
    private List<Department> departments;
}