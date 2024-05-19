package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.resource.dto.BudgetBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.util.UUID;

public interface BudgetService {
    Multi<BudgetDto> getBudgets(final int page, final int limit);
    Uni<BudgetDto> getActiveBudgetForUser();
    Uni<Response> createBudget(final BudgetBaseDto budgetBaseDto, final UriInfo uriInfo);
    Uni<Response> updateBudget(final UUID budgetId, final BudgetBaseDto budgetDto, final UriInfo uriInfo);
    Uni<Response> updateActiveBudget(BudgetBaseDto budgetDto);
}
