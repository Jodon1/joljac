package com.example.dormmatching.repository;

import com.example.dormmatching.entity.user.StudentStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface StudentStatusRepository extends JpaRepository<StudentStatus, Integer> {
    Optional<StudentStatus> findByCode(String code);
}