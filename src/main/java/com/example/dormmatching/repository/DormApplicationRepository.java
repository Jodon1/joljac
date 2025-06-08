package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.DormApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface DormApplicationRepository extends JpaRepository<DormApplication, Long> {
    List<DormApplication> findByPeriodPeriodId(Integer periodId);
    Optional<DormApplication> findByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);
    boolean existsByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);

    @Query("SELECT DISTINCT da FROM DormApplication da WHERE da.period.periodId = :periodId")
    List<DormApplication> findDistinctByPeriodPeriodId(@Param("periodId") Integer periodId);

    // ↓ 추가: 페이지 지원
    @Query("SELECT DISTINCT da FROM DormApplication da WHERE da.period.periodId = :periodId")
    Page<DormApplication> findDistinctByPeriodPeriodId(@Param("periodId") Integer periodId, Pageable pageable);
}
