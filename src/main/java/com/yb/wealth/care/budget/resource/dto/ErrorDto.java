package com.yb.wealth.care.budget.resource.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@Builder
public class ErrorDto {
    private List<String> messages;
}
