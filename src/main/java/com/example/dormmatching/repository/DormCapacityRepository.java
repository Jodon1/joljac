package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.DormCapacity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface DormCapacityRepository extends JpaRepository<DormCapacity, Long> {
    List<DormCapacity> findByPeriodPeriodId(Integer periodId);
}
