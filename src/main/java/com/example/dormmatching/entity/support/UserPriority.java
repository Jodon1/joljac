package com.example.dormmatching.entity.support;

import com.example.dormmatching.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Setter
@Getter
@Table(name = "user_priority")
@IdClass(UserPriorityId.class)
public class UserPriority {

    @Id
    @Column(name = "user_id")
    private Long userId;

    @Id
    @Column(name = "criteria_id")
    private Integer criteriaId;

    @ManyToOne
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "criteria_id", insertable = false, updatable = false)
    private PriorityCriteria criteria;

    @Column(name = "proof_submitted")
    private LocalDate proofSubmitted;
}
