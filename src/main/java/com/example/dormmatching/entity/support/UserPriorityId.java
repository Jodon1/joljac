package com.example.dormmatching.entity.support;

import java.io.Serializable;
import java.util.Objects;

/**
 * Composite primary key class for UserPriority entity.
 */
public class UserPriorityId implements Serializable {
    private Long userId;
    private Integer criteriaId;

    public UserPriorityId() {}

    public UserPriorityId(Long userId, Integer criteriaId) {
        this.userId = userId;
        this.criteriaId = criteriaId;
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserPriorityId that = (UserPriorityId) o;
        return Objects.equals(userId, that.userId) &&
                Objects.equals(criteriaId, that.criteriaId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(userId, criteriaId);
    }
}
