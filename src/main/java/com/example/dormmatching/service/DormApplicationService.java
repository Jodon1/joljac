package com.example.dormmatching.service;

import com.example.dormmatching.dto.DormApplicationRequest;
import com.example.dormmatching.entity.application.DormApplication;

public interface DormApplicationService {
    DormApplication applyForDorm(DormApplicationRequest request, Long userId);
    boolean cancelApplication(Long userId, Integer periodId);
}
