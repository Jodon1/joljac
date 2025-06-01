package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.UserPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPriorityRepository extends JpaRepository<UserPriority, Long> {
    List<UserPriority> findByUserIdAndProofSubmittedTrue(Long userId);
}
