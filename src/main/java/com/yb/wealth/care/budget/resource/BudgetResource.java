package com.yb.wealth.care.budget.resource;

import com.yb.wealth.care.budget.resource.dto.*;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;


public interface BudgetResource {

    Multi<BudgetDto> getBudgets(final int page,
                                final int limit);

    Multi<BudgetExpenseDetailsItemDto> getBudgetDetails(final String budgetId);

    Uni<Response> createBudget(@Valid final BudgetBaseDto budgetDto,
                               final UriInfo uriInfo);

    Uni<Response> updateBudget(String budgetId,
                               @Valid final BudgetBaseDto budgetDto,
                               final UriInfo uriInfo);

    Uni<Response> updateActiveBudget(@Valid BudgetBaseDto budgetDto);

    Uni<Response> addBudgetDetailItem(@Valid final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto);

    Uni<Response> removeBudgetDetailItem(final String budgetId,
                                         final String budgetDetailItemId);

    Uni<Response> updateBudgetDetailItem(final String budgetId,
                                         final String budgetDetailItemId,
                                         @Valid final BudgetExpenseDetailsBaseItemDto budgetDetailItem);
}
