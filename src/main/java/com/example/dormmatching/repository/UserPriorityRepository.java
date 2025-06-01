package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.UserPriority;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface UserPriorityRepository extends JpaRepository<UserPriority, Long> {
    /**
     * user_id = :userId 이고 proof_submitted IS NOT NULL 인 레코드를 모두 조회
     */
    List<UserPriority> findByUserIdAndProofSubmittedIsNotNull(Long userId);
}
