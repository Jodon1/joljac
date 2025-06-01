package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.ApplicationPeriod;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ApplicationPeriodRepository extends JpaRepository<ApplicationPeriod, Integer> {
    // 기본 CRUD 메서드만으로 충분합니다.
}
