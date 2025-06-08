package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.DormCapacity;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DormCapacityRepository extends JpaRepository<DormCapacity, Long> {
    Page<DormCapacity> findByPeriodPeriodId(Integer periodId, Pageable pageable);

    List<DormCapacity> findByPeriodPeriodId(Integer periodId);
}
