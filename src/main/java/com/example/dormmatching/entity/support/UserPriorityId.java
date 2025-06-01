package com.example.dormmatching.entity.support;

import java.io.Serializable;
import java.util.Objects;

/**
 * UserPriority 엔티티의 @IdClass에 대응하는 복합 키 클래스
 */
public class UserPriorityId implements Serializable {
    private Long userId;
    private Integer criteriaId;

    public UserPriorityId() {}

    public UserPriorityId(Long userId, Integer criteriaId) {
        this.userId = userId;
        this.criteriaId = criteriaId;
    }

    // equals 및 hashCode 반드시 구현
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof UserPriorityId)) return false;
        UserPriorityId that = (UserPriorityId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(criteriaId, that.criteriaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, criteriaId);
    }

    // getter / setter
    public Long getUserId() {
        return userId;
    }
    public void setUserId(Long userId) {
        this.userId = userId;
    }

    public Integer getCriteriaId() {
        return criteriaId;
    }
    public void setCriteriaId(Integer criteriaId) {
        this.criteriaId = criteriaId;
    }
}
