package com.example.dormmatching.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "users")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class UserEntity {

    @Id
    @Column(name = "identifier", length = 50, nullable = false)
    private String identifier;

    @Column(name = "password", length = 255, nullable = false)
    private String password;

    @Column(name = "name", length = 50, nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "gender", length = 2, nullable = false)
    private Gender gender;

    @Column(name = "birth_year", length = 4, nullable = false)
    private String birthYear;

    @Column(name = "student_number", length = 7, nullable = false, unique = true)
    private String studentNumber;

    @Column(name = "grade", nullable = false)
    private Integer grade;

    @Column(name = "department", length = 100)
    private String department;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private UserProfile profile;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private DesiredRoommateProfile desiredProfile;

    @Column(name = "RT", length = 500)
    private String refreshToken;

    public enum Gender { 남, 여 }
}
