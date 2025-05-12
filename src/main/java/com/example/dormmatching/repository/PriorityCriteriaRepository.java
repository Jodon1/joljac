package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.PriorityCriteria;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface PriorityCriteriaRepository extends JpaRepository<PriorityCriteria, Integer> {
    Optional<PriorityCriteria> findByCode(String code);
}
