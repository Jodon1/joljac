package com.example.dormmatching.entity.application;

import com.example.dormmatching.entity.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@JsonIgnoreProperties({"hibernateLazyInitializer","handler"})
@Entity
@Table(name = "selection_result")
@Getter @Setter
public class SelectionResult {

    // ① 자동 생성되는 기본키(selection_id)를 새로 만들어 준다.
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "selection_id")
    private Long selectionId;

    // ② userId는 더 이상 @Id가 아니므로 그냥 일반 컬럼으로 선언
    @Column(name = "user_id", nullable = false)
    private Long userId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", insertable = false, updatable = false)
    @JsonIgnore
    private User user;

    // ③ periodId도 일반 컬럼으로 선언
    @Column(name = "period_id", nullable = false)
    private Integer periodId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "period_id", insertable = false, updatable = false)
    @JsonIgnoreProperties({ "applications", "capacities", "hibernateLazyInitializer", "handler" })
    private ApplicationPeriod period;

    @Column(name = "selection_score")
    private Double selectionScore;

    @Column(name = "selection_rank")
    private Integer selectionRank;

    @Column(name = "selected")
    private Boolean selected;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;
}
