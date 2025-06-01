package com.example.dormmatching.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DormApplicationRequest {
    @NotNull
    private Integer periodId;
}
