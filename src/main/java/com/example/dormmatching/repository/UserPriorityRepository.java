package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.UserPriority;
import com.example.dormmatching.entity.support.UserPriorityId;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserPriorityRepository extends JpaRepository<UserPriority, UserPriorityId> {
    List<UserPriority> findAllByUserUserId(Long userId);
}
