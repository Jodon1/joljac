package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.SelectionResult;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SelectionResultRepository extends JpaRepository<SelectionResult, Long> {
    // 필요 시 추가 메서드 선언 가능
}
