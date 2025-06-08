package com.example.dormmatching.service;

import com.example.dormmatching.dto.DormApplicationRequest;
import com.example.dormmatching.entity.application.DormApplication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DormApplicationService {
    DormApplication applyForDorm(DormApplicationRequest request, Long userId);
    boolean cancelApplication(Long userId, Integer periodId);
    List<DormApplication> getApplicationsByPeriod(Integer periodId);

    Page<DormApplication> getApplicationsByPeriod(Integer periodId, Pageable pageable);
}
