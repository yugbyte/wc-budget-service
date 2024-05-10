package com.yb.wealth.care.budget.resource.dto;

import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Getter
@Setter
public class BudgetDetailsBaseDto {
    private String expenseBudgetId;
    private String expenseName;
    private Double budgetAmount;
    private Boolean isRecurring;
    private String expenseCategory;
    private String icon;
}
