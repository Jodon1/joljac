package com.example.dormmatching.repository;

import com.example.dormmatching.entity.UserEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<UserEntity, String> {
    boolean existsByIdentifier(String identifier);
    boolean existsByStudentNumber(String studentNumber);
    Optional<UserEntity> findByIdentifier(String identifier);
}
