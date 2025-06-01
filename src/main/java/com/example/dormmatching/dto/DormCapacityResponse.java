package com.example.dormmatching.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
public class DormCapacityResponse {
    private Long capacityId;
    private Long periodId;
    private String gender;
    private Integer capacity;

    @Builder
    public DormCapacityResponse(Long capacityId, Long periodId, String gender, Integer capacity) {
        this.capacityId = capacityId;
        this.periodId = periodId;
        this.gender = gender;
        this.capacity = capacity;
    }
}
