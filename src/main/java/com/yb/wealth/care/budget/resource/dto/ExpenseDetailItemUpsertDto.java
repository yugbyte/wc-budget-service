package com.yb.wealth.care.budget.resource.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class ExpenseDetailItemUpsertDto {
    @NotBlank(message = "Expense Name is Required")
    private String expenseName;
    @NotNull(message = "Budget Amount is Required")
    @Min(value = 1, message = "Budget Amount should be at least 1")
    private Double budgetAmount;
    private Boolean isRecurring;
    @NotBlank(message = "Expense Category is Required")
    private String category;
    private List<String> tags;
}
