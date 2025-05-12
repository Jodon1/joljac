package com.example.dormmatching.entity.application;

import com.example.dormmatching.entity.user.User;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Setter
@Getter
@Table(name = "selection_result")
public class SelectionResult {
    @Id
    @Column(name = "user_id")
    private Long userId;

    @OneToOne
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne
    @JoinColumn(name = "period_id", nullable = false)
    private ApplicationPeriod period;

    @Column(name = "selection_score")
    private Double selectionScore;

    @Column(name = "selection_rank")
    private Integer selectionRank;

    @Column(name = "selected")
    private Boolean selected;

    @Column(name = "notified_at")
    private LocalDateTime notifiedAt;

    // getters and setters...
}
