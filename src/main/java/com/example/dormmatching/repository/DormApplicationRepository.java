package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.DormApplication;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface DormApplicationRepository extends JpaRepository<DormApplication, Long> {
    List<DormApplication> findAllByPeriodPeriodId(Integer periodId);
    List<DormApplication> findAllByUserUserId(Long userId);
}
