package com.example.dormmatching.repository;

import com.example.dormmatching.entity.support.UserContact;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface UserContactRepository extends JpaRepository<UserContact, Long> {
    List<UserContact> findAllByUserUserId(Long userId);
}
