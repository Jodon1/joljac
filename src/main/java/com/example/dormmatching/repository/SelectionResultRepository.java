package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.SelectionResult;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SelectionResultRepository extends JpaRepository<SelectionResult, Long> {
    List<SelectionResult> findByPeriodPeriodId(Integer periodId);
    Optional<SelectionResult> findByUserUserIdAndPeriodPeriodId(Long userId, Integer periodId);

    // 포기 처리 시 후보군 중 최고 점수를 찾기 위한 메서드 예시
    Optional<SelectionResult> findTopByPeriodPeriodIdAndSelectedFalseAndUserGenderOrderBySelectionScoreDesc(
            Integer periodId, String gender);
}
