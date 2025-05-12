package com.example.dormmatching.entity.support;

import com.example.dormmatching.entity.user.User;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Entity
@Setter
@Getter
@Table(name = "admission_round")
public class AdmissionRound {
    @Id
    @Column(name = "round_id")
    private Integer roundId;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "admissionRound")
    private Set<User> users;

    // getters and setters...
}