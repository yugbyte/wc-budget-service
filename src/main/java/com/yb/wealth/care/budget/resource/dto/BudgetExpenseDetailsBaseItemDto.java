package com.yb.wealth.care.budget.resource.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class BudgetExpenseDetailsBaseItemDto {
    @NotBlank(message = "Budget Expense Name is Required")
    private String expenseName;
    @NotNull(message = "Budget Amount is Required")
    private Double budgetAmount;
    private Boolean isRecurring;
    @NotBlank(message = "Expense Category is Required")
    private String category;
    private String icon;
    private List<String> tags;
}
