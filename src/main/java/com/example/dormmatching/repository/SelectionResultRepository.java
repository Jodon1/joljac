package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.SelectionResult;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface SelectionResultRepository extends JpaRepository<SelectionResult, Long> {
    List<SelectionResult> findAllByPeriodPeriodId(Integer periodId);
}
