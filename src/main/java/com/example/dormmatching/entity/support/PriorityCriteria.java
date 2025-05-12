package com.example.dormmatching.entity.support;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "priority_criteria")
public class PriorityCriteria {
    @Id
    @Column(name = "criteria_id")
    private Integer criteriaId;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 100)
    private String name;

    @OneToMany(mappedBy = "criteria")
    private Set<UserPriority> userPriorities;

    // getters and setters...
}
