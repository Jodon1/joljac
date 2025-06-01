package com.example.dormmatching.service;

import com.example.dormmatching.dto.DormCapacityRequest;
import com.example.dormmatching.entity.application.DormCapacity;

import java.util.List;

public interface DormCapacityService {
    DormCapacity createOrUpdateCapacity(DormCapacityRequest request);
    List<DormCapacity> getCapacitiesByPeriod(Integer periodId);
    void deleteCapacity(Long capacityId);
}
