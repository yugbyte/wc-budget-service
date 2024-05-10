package com.yb.wealth.care.budget.resource;

import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import com.yb.wealth.care.budget.service.BudgetService;
import com.yb.wealth.care.budget.util.CommonUtil;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
@Path("/budgets")
public class BudgetResourceImpl implements BudgetResource {
    private final BudgetService budgetService;

    @GET
    public Multi<BudgetDto> getBudgets(@QueryParam("page") @DefaultValue("0") int page, @QueryParam("limit")@DefaultValue("5") int limit) {
        log.info("GETTING BUDGETS");
        if (page < 0 || limit <= 0) {
            throw new BadRequestException("Invalid page or limit");
        }
        return budgetService.getBudgets(page, limit);
    }

    @GET
    @Path("/{budgetId}")
    public Multi<BudgetDetailsDto> getBudgetDetails(String budgetId) {
        UUID id = CommonUtil.convertToUUID(budgetId)
                .orElseThrow(() -> new BadRequestException("Invalid Budget Id"));
       return budgetService.getBudgetDetails(id);
    }

    @POST
    public Uni<Void> createBudget(BudgetDetailsBaseDto budgetDetails) {
        return budgetService.createBudget(budgetDetails);
    }
}
