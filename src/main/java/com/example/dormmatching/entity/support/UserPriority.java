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

    /**
     * proof_submitted 칼럼이 LocalDate 형식으로 들어오며,
     * NULL이 아니면 “증빙서류 제출됨”으로 간주합니다.
     */
    @Column(name = "proof_submitted")
    private LocalDate proofSubmitted;
}
