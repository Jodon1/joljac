package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.AdmissionRound;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

public interface AdmissionRoundRepository extends JpaRepository<AdmissionRound, Integer> {
    Optional<AdmissionRound> findByCode(String code);
}
