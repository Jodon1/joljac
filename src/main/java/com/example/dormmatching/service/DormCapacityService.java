package com.example.dormmatching.service;

import com.example.dormmatching.dto.DormCapacityRequest;
import com.example.dormmatching.entity.application.DormCapacity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface DormCapacityService {
    DormCapacity createOrUpdateCapacity(DormCapacityRequest request);

    List<DormCapacity> findByPeriodPeriodId(Integer periodId);

    Page<DormCapacity> getCapacitiesByPeriod(Integer periodId, Pageable pageable);

    void deleteCapacity(Long capacityId);
}
