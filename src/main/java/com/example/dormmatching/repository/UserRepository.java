package com.example.dormmatching.repository;

import com.example.dormmatching.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentNumber(String studentNumber);

    @Query("""
      SELECT u
      FROM User u
      JOIN u.applications da
      WHERE da.period.periodId = :periodId
        AND u.status.statusId IN (1, 2, 3, 4)
    """)
    List<User> findEnrolledStudentsByPeriod(@Param("periodId") Integer periodId);
}