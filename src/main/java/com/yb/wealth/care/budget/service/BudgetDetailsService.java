package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsBaseItemDto;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import com.yb.wealth.care.budget.resource.dto.ExpenseDetailItemUpsertDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;

import java.util.UUID;

public interface BudgetDetailsService {
    Multi<BudgetExpenseDetailsItemDto> getBudgetDetails(final UUID budgetId);
    Uni<Response> addBudgetDetailItem(final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto);

    Uni<Response> removeBudgetDetailItem(final UUID budgetDetailItemId);

    Uni<Response> updateBudgetDetailItem(final UUID budgetDetailItemId,
                                         final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto);
}
