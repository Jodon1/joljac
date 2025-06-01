package com.example.dormmatching.repository;

import com.example.dormmatching.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByStudentNumber(String studentNumber);
    Optional<User> findByRefreshToken(String refreshToken);

    /**
     * statusId = 1 (재학생) 이고,
     * u.applications.period.periodId = :periodId 인 User 를 모두 조회
     */
    @Query("""
      SELECT u
      FROM User u
      JOIN u.applications da
      WHERE u.status.statusId = 1
        AND da.period.periodId = :periodId
    """)
    List<User> findEnrolledStudentsByPeriod(@Param("periodId") Integer periodId);
}
