package com.yb.wealth.care.budget.resource.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ErrorDto {
    private String message;
}
