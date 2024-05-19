package com.yb.wealth.care.budget.resource.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetBaseDto {
    @NotBlank(message = "Budget Name is Required")
    private String name;
    private String currency;
    private List<String> tags;
}
