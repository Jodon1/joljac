package com.example.dormmatching.service.impl;

import com.example.dormmatching.dto.DormApplicationRequest;
import com.example.dormmatching.entity.application.ApplicationPeriod;
import com.example.dormmatching.entity.application.DormApplication;
import com.example.dormmatching.entity.user.User;
import com.example.dormmatching.repository.ApplicationPeriodRepository;
import com.example.dormmatching.repository.DormApplicationRepository;
import com.example.dormmatching.repository.UserRepository;
import com.example.dormmatching.service.DormApplicationService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Service
@Transactional
public class DormApplicationServiceImpl implements DormApplicationService {

    private final DormApplicationRepository applicationRepository;
    private final UserRepository userRepository;
    private final ApplicationPeriodRepository periodRepository;

    public DormApplicationServiceImpl(
            DormApplicationRepository applicationRepository,
            UserRepository userRepository,
            ApplicationPeriodRepository periodRepository
    ) {
        this.applicationRepository = applicationRepository;
        this.userRepository = userRepository;
        this.periodRepository = periodRepository;
    }

    @Override
    public DormApplication applyForDorm(DormApplicationRequest request, Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found: " + userId));
        ApplicationPeriod period = periodRepository.findById(request.getPeriodId())
                .orElseThrow(() -> new RuntimeException("ApplicationPeriod not found: " + request.getPeriodId()));

        boolean already = applicationRepository.existsByUserUserIdAndPeriodPeriodId(userId, request.getPeriodId());
        if (already) {
            throw new RuntimeException("이미 이 기간에 신청하셨습니다.");
        }

        if (LocalDateTime.now().isAfter(period.getEndDate().atStartOfDay())) {
            throw new RuntimeException("신청 기간이 마감되었습니다: " + period.getEndDate());
        }

        DormApplication da = new DormApplication();
        da.setUser(user);
        da.setPeriod(period);
        da.setAppliedAt(LocalDateTime.now());
        da.setProofSubmitted(false);

        return applicationRepository.save(da);
    }

    @Override
    public boolean cancelApplication(Long userId, Integer periodId) {
        DormApplication da = applicationRepository
                .findByUserUserIdAndPeriodPeriodId(userId, periodId)
                .orElseThrow(() -> new RuntimeException("해당 신청 내역이 없습니다."));
        applicationRepository.delete(da);
        return true;
    }
    @Override
    public List<DormApplication> getApplicationsByPeriod(Integer periodId) {
        periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("ApplicationPeriod not found: " + periodId));
        return applicationRepository.findDistinctByPeriodPeriodId(periodId);
    }
    @Override
    public Page<DormApplication> getApplicationsByPeriod(Integer periodId, Pageable pageable) {
        periodRepository.findById(periodId)
                .orElseThrow(() -> new IllegalArgumentException("ApplicationPeriod not found: " + periodId));
        return applicationRepository.findDistinctByPeriodPeriodId(periodId, pageable);
    }
}
