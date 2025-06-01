package com.example.dormmatching.entity.application;

import com.example.dormmatching.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@Table(name = "dorm_application")
public class DormApplication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "application_id")
    private Long applicationId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private ApplicationPeriod period;

    @Column(name = "applied_at")
    private LocalDateTime appliedAt;

    @Column(name = "proof_submitted")
    private Boolean proofSubmitted;
}
