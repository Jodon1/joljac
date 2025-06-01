package com.example.dormmatching.dto;

import lombok.Builder;
import lombok.Getter;

import java.time.LocalDate;

@Getter
public class ApplicationPeriodResponse {
    private Long periodId;
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;

    @Builder
    public ApplicationPeriodResponse(Long periodId, String name, LocalDate startDate, LocalDate endDate) {
        this.periodId = periodId;
        this.name = name;
        this.startDate = startDate;
        this.endDate = endDate;
    }
}
