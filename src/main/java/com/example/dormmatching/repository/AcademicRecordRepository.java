package com.example.dormmatching.repository;

import com.example.dormmatching.entity.record.AcademicRecord;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AcademicRecordRepository extends JpaRepository<AcademicRecord, Long> {
}
