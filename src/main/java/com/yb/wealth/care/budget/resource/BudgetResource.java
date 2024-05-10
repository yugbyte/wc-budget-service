package com.yb.wealth.care.budget.resource;

import com.yb.wealth.care.budget.resource.dto.BudgetDetailsBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;


public interface BudgetResource {

    Multi<BudgetDto> getBudgets(int page, int limit);

    Multi<BudgetDetailsDto> getBudgetDetails(String budgetId);

    Uni<Void> createBudget(BudgetDetailsBaseDto budgetDetails);
}
