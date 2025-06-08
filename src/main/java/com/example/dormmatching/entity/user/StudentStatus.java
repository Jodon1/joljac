package com.example.dormmatching.entity.user;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@JsonIgnoreProperties("users")
@Entity
@Setter
@Getter
@Table(name = "student_status")
public class StudentStatus {
    @Id
    @Column(name = "status_id")
    private Integer statusId;

    @Column(nullable = false, unique = true, length = 20)
    private String code;

    @Column(length = 50)
    private String name;

    @OneToMany(mappedBy = "status")
    private Set<User> users;

    // getters and setters...
}