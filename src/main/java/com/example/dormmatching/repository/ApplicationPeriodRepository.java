package com.example.dormmatching.repository;

import com.example.dormmatching.entity.application.ApplicationPeriod;
import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Pageable;

public interface ApplicationPeriodRepository extends JpaRepository<ApplicationPeriod, Integer> {
    Page<ApplicationPeriod> findAll(Pageable pageable);


}
