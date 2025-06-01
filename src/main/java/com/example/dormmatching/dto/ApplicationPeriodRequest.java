package com.example.dormmatching.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter @Setter
public class ApplicationPeriodRequest {
    private String name;
    private LocalDate startDate;
    private LocalDate endDate;
}
