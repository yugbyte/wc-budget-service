package com.yb.wealth.care.budget.resource;

import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.resource.dto.*;
import com.yb.wealth.care.budget.service.BudgetDetailsService;
import com.yb.wealth.care.budget.service.BudgetService;
import com.yb.wealth.care.budget.util.CommonUtil;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
@Path("/budgets")
public class BudgetResourceImpl implements BudgetResource {
    private final BudgetService budgetService;
    private final BudgetDetailsService budgetDetailsService;

    @GET
    public Multi<BudgetDto> getBudgets(@QueryParam("page") @DefaultValue("0") final int page,
                                       @QueryParam("limit") @DefaultValue("5") final int limit) {
        log.debug("GETTING BUDGETS");
        if (page < 0 || limit <= 0) {
            throw new BadRequestException("Invalid page or limit");
        }
        return budgetService.getBudgets(page, limit);
    }

    @GET
    @Path("/_active")
    public  Uni<BudgetDto> getActiveBudget() {
        log.debug("GETTING ACTIVE BUDGET INFO");
        return budgetService.getActiveBudgetForUser();
    }

    @GET
    @Path("/{budgetId}")
    public Multi<BudgetExpenseDetailsItemDto> getBudgetDetails(final String budgetId) {
        log.info("GETTING BUDGET DETAILS FOR {}", budgetId);
        UUID id = CommonUtil.convertToUUID(budgetId)
                .orElseThrow(() -> new BadRequestException("Invalid Budget Id"));
       return budgetDetailsService.getBudgetDetails(id);
    }

    @POST
    public Uni<Response> createBudget(@Valid final BudgetBaseDto budgetDto,  @Context final UriInfo uriInfo) {
        log.info("CREATING BUDGET");
        return budgetService.createBudget(budgetDto, uriInfo);
    }

    @PUT
    @Path("/{budgetId}")
    @Override
    public Uni<Response> updateBudget(String budgetId, BudgetBaseDto budgetDto, UriInfo uriInfo) {
        log.info("UPDATING BUDGET DETAILS FOR {}", budgetId);
        UUID id = CommonUtil.convertToUUID(budgetId)
                .orElseThrow(() -> new BadRequestException("Invalid Budget Id"));
        return budgetService.updateBudget(id, budgetDto, uriInfo);
    }

    @PUT
    @Path("/_active")
    @Override
    public Uni<Response> updateActiveBudget(@Valid BudgetBaseDto budgetDto) {
        log.info("UPDATING BUDGET DETAILS FOR ACTIVE BUDGET");
        return budgetService.updateActiveBudget(budgetDto);
    }

    @POST
    @Path("/_active")
    @Override
    public Uni<Response> addBudgetDetailItem(@Valid ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        log.info("ADDING BUDGET DETAIL ITEM");
        return budgetDetailsService.addBudgetDetailItem(expenseDetailItemUpsertDto);
    }

    @Override
    public Uni<Response> removeBudgetDetailItem(String budgetId, String budgetDetailItemId) {
        return null;
    }

    @Override
    public Uni<Response> updateBudgetDetailItem(String budgetId, String budgetDetailItemId, BudgetExpenseDetailsBaseItemDto budgetDetailItem) {
        return null;
    }

}
