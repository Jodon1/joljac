package com.example.dormmatching.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DormCapacityRequest {
    @NotNull
    private Integer periodId;

    @NotNull
    @Pattern(regexp = "M|F", message = "gender는 'M' 또는 'F'만 가능합니다.")
    private String gender;

    @NotNull
    @Min(value = 1, message = "capacity는 최소 1명 이상이어야 합니다.")
    private Integer capacity;
}
