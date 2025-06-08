package com.example.dormmatching.entity.user;

import com.example.dormmatching.entity.application.DormApplication;
import com.example.dormmatching.entity.application.SelectionResult;
import com.example.dormmatching.entity.record.AcademicRecord;
import com.example.dormmatching.entity.record.HealthDiscipline;
import com.example.dormmatching.entity.support.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Setter
@Getter
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "student_number", nullable = false, unique = true, length = 20)
    private String studentNumber;

    @Column(nullable = false, length = 60)
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;

    @Column(nullable = false, length = 50)
    private String name;

    @Column(name = "birth_date", nullable = false)
    private LocalDate birthDate;

    @Column(nullable = false, length = 1)
    private String gender;

    @ManyToOne
    @JoinColumn(name = "status_id", nullable = false)
    @JsonIgnoreProperties("users")
    private StudentStatus status;


    @ManyToOne
    @JoinColumn(name = "admission_round_id")
    private AdmissionRound admissionRound;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    private Region region;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(nullable = false)
    private Boolean international;

    @Column(length = 200)
    private String address;

    @CreatedDate
    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "user")
    private Set<UserContact> contacts;

    @OneToMany(mappedBy = "user")
    private Set<UserPriority> priorities;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private HealthDiscipline healthDiscipline;

    @OneToOne(mappedBy = "user", cascade = CascadeType.ALL)
    private AcademicRecord academicRecord;

    @OneToMany(mappedBy = "user")
    private Set<DormApplication> applications;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private Set<SelectionResult> selectionResults = new HashSet<>();

    @Column(name = "refresh_token", length = 255)
    private String refreshToken;

    @Column(name = "phone_number", length = 20)
    private String phoneNumber;

    @Column(name = "password_initialized", nullable = false)
    private boolean passwordInitialized = false;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    @Column(name = "grade")
    private Integer grade; // 새로 추가한 컬럼
}
