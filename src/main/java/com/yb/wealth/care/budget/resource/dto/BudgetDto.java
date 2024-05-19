package com.yb.wealth.care.budget.resource.dto;

import com.yb.wealth.care.budget.constant.Currency;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.util.List;

@Getter
@Setter
public class BudgetDto extends BudgetBaseDto {
    private String id;
    private String status;
    private List<BudgetExpenseDetailsItemDto> budgetExpenseItems;
}
