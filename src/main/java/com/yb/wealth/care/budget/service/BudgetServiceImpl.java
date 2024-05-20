package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.constant.BudgetStatus;
import com.yb.wealth.care.budget.constant.Currency;
import com.yb.wealth.care.budget.constant.ErrorMessages;
import com.yb.wealth.care.budget.constant.ExpenseCategories;
import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.exception.ExceptionHandler;
import com.yb.wealth.care.budget.mapper.BudgetMapper;
import com.yb.wealth.care.budget.repository.ExpenseBudgetRepository;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.resource.dto.BudgetBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriBuilder;
import jakarta.ws.rs.core.UriInfo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.time.Instant;
import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class BudgetServiceImpl implements BudgetService {
    private final ExpenseBudgetRepository expenseBudgetRepository;
    private final BudgetMapper budgetMapper;
    private final ExpenseCategories expenseCategories;

    @Override
    public Multi<BudgetDto> getBudgets(final int page, final int limit) {
        log.debug("INSIDE getBudgets: {} {}", page, limit);
        return expenseBudgetRepository.findWithPages(page, limit)
                .onItem()
                .transformToMulti(item -> Multi.createFrom().iterable(item))
                .map(budgetMapper::toBudgetDto);
    }

    @Override
    public Uni<BudgetDto> getActiveBudgetForUser() {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                .onItem()
                .ifNotNull()
                .transform(budgetMapper::toBudgetDto)
                .map(budgetDto -> {
                    budgetDto.getBudgetExpenseItems().forEach( expenseDetailsItemDto -> {
                       final String icon = expenseCategories.getExpenseCategoryByName(expenseDetailsItemDto.getCategory()).getIcon();
                       expenseDetailsItemDto.setIcon(icon);
                    });
                    return budgetDto;
                })
                .onFailure()
                .transform(ExceptionHandler::handleError);
    }

    @Override
    @WithTransaction
    public Uni<Response> createBudget(final BudgetBaseDto budgetBaseDto, final UriInfo uriInfo) {
        validateCurrency(budgetBaseDto.getCurrency());
        ExpenseBudget expenseBudget = budgetMapper.toExpenseBudget(budgetBaseDto);
        expenseBudget.setUserId(1);
        expenseBudget.setStatus(BudgetStatus.A.name());
        log.info("Persisting: {}", expenseBudget);
        return expenseBudgetRepository
                .findBudgetForUserWithBudgetName(expenseBudget.getUserId(), expenseBudget.getName())
                .onItem()
                .ifNotNull()
                .failWith(() -> new BadRequestException(String.format(ErrorMessages.ERROR_DUPLICATE_BUDGET, budgetBaseDto.getName())))
                .replaceWith(expenseBudgetRepository.deactivateCurrentBudget())
                .flatMap(isSuccess -> this.saveBudget(expenseBudget, uriInfo))
                .onFailure()
                .transform(ExceptionHandler::handleError);
    }

    @Override
    @WithTransaction
    public Uni<Response> updateBudget(final UUID budgetId, final BudgetBaseDto budgetDto, final UriInfo uriInfo) {
        if (StringUtils.isNotBlank(budgetDto.getCurrency())) {
            validateCurrency(budgetDto.getCurrency());
        }
        return expenseBudgetRepository.findByUserAndBudgetId(2, budgetId)
                .map(expenseBudget -> this.setExpenseBudgetWithNewValues(budgetDto, expenseBudget))
                .flatMap(expenseBudgetRepository::persist)
                .flatMap(expenseBudget -> Uni.createFrom().item(budgetMapper.toBudgetDto(expenseBudget)))
                .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                .onFailure()
                .transform(ExceptionHandler::handleError);
    }

    @Override
    @WithTransaction
    public Uni<Response> updateActiveBudget(BudgetBaseDto budgetDto) {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                .map(expenseBudget -> this.setExpenseBudgetWithNewValues(budgetDto, expenseBudget))
                .flatMap(expenseBudgetRepository::persist)
                .flatMap(expenseBudget -> Uni.createFrom().item(budgetMapper.toBudgetDto(expenseBudget)))
                .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                .onFailure()
                .transform(ExceptionHandler::handleError);
    }

    private ExpenseBudget setExpenseBudgetWithNewValues(final BudgetBaseDto budgetDto, ExpenseBudget expenseBudget) {
        if (StringUtils.isNotBlank(budgetDto.getName())) {
            expenseBudget.setName(budgetDto.getName());
        }
        if (StringUtils.isNotBlank(budgetDto.getCurrency())){
            expenseBudget.setName(budgetDto.getName());
        }
        expenseBudget.setCurrency(budgetDto.getCurrency());
        if (CollectionUtils.isNotEmpty(budgetDto.getTags())) {
            expenseBudget.setTags(String.join(",", budgetDto.getTags()));
        }
        expenseBudget.setUpdatedTime(Instant.now());
        return expenseBudget;
    }

    private void validateCurrency(String currencyStr) {
        Currency currency = Currency.getFromString(currencyStr);
        if (currency == null) {
            throw new BadRequestException(String.format(ErrorMessages.INVALID_CURRENCY ,currencyStr));
        }
    }

    private Uni<Response> saveBudget(final ExpenseBudget expenseBudget, final UriInfo uriInfo) {
        log.info("SAVING BUDGET: {}", expenseBudget);
        return expenseBudgetRepository.persist(expenseBudget)
                .map(savedBudget -> {
                    UriBuilder uriBuilder = uriInfo.getAbsolutePathBuilder();
                    uriBuilder.path(savedBudget.getId().toString());
                    String resourceUrl = uriBuilder.build().toString();
                    return Response.created(UriBuilder.fromPath(resourceUrl).build()).build();
                }).onFailure()
                .transform(Unchecked.function(error -> {
                    log.error(ErrorMessages.ERROR_CREATING_BUDGET, error);
                    throw new WebApplicationException(ErrorMessages.ERROR_CREATING_BUDGET);
                }));
    }

}
