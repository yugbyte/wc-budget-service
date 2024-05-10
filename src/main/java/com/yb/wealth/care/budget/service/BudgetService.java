package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.resource.dto.BudgetDetailsBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;

import java.util.List;
import java.util.UUID;

public interface BudgetService {
    Multi<BudgetDto> getBudgets(int page, int limit);
    Multi<BudgetDetailsDto> getBudgetDetails(UUID budgetId);
    Uni<Void> createBudget(BudgetDetailsBaseDto budgetDetails);
}
