package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.SelectionResult;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface SelectionResultRepository extends JpaRepository<SelectionResult, Long> {
    List<SelectionResult> findByPeriodPeriodId(Integer periodId);

    @Modifying
    @Transactional
    @Query("DELETE FROM SelectionResult sr WHERE sr.periodId = :periodId")
    void deleteByPeriodPeriodId(@Param("periodId") Integer periodId);

    Optional<SelectionResult> findByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);

    @Query("SELECT DISTINCT sr FROM SelectionResult sr WHERE sr.period.periodId = :periodId")
    Page<SelectionResult> findDistinctByPeriodPeriodId(@Param("periodId") Integer periodId, Pageable pageable);

    // 포기 처리 시 후보군 중 최고 점수를 찾기 위한 메서드 예시
    Optional<SelectionResult> findTopByPeriodPeriodIdAndSelectedFalseAndUserGenderOrderBySelectionScoreDesc(
            Integer periodId, String gender);
}
