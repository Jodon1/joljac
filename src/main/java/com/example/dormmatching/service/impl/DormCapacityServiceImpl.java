package com.example.dormmatching.service.impl;

import com.example.dormmatching.dto.DormCapacityRequest;
import com.example.dormmatching.entity.application.ApplicationPeriod;
import com.example.dormmatching.entity.application.DormCapacity;
import com.example.dormmatching.repository.ApplicationPeriodRepository;
import com.example.dormmatching.repository.DormCapacityRepository;
import com.example.dormmatching.service.DormCapacityService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class DormCapacityServiceImpl implements DormCapacityService {

    private final DormCapacityRepository capacityRepository;
    private final ApplicationPeriodRepository periodRepository;

    public DormCapacityServiceImpl(
            DormCapacityRepository capacityRepository,
            ApplicationPeriodRepository periodRepository
    ) {
        this.capacityRepository = capacityRepository;
        this.periodRepository = periodRepository;
    }

    @Override
    public DormCapacity createOrUpdateCapacity(DormCapacityRequest request) {
        ApplicationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new RuntimeException("ApplicationPeriod not found: " + request.getPeriodId()));

        List<DormCapacity> existing = capacityRepository.findByPeriodPeriodId(request.getPeriodId());
        DormCapacity target = existing.stream()
                .filter(c -> c.getGender().equals(request.getGender()))
                .findFirst()
                .orElseGet(DormCapacity::new);

        target.setPeriod(period);
        target.setGender(request.getGender());
        target.setCapacity(request.getCapacity());

        return capacityRepository.save(target);
    }

    @Override
    public List<DormCapacity> getCapacitiesByPeriod(Integer periodId) {
        return capacityRepository.findByPeriodPeriodId(periodId);
    }

    @Override
    public void deleteCapacity(Long capacityId) {
        capacityRepository.deleteById(capacityId);
    }
}
