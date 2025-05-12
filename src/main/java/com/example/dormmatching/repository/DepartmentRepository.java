package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.Department;
import org.springframework.data.jpa.repository.JpaRepository;

public interface DepartmentRepository extends JpaRepository<Department, Integer> {
}