package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.DormApplication;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DormApplicationRepository extends JpaRepository<DormApplication, Long> {
    List<DormApplication> findByPeriodPeriodId(Integer periodId);
    Optional<DormApplication> findByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);
    boolean existsByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);
}
