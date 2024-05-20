package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.constant.ErrorMessages;
import com.yb.wealth.care.budget.constant.ExpenseCategories;
import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.exception.ExceptionHandler;
import com.yb.wealth.care.budget.mapper.BudgetDetailsMapper;
import com.yb.wealth.care.budget.mapper.BudgetMapper;
import com.yb.wealth.care.budget.repository.ExpenseBudgetDetailRepository;
import com.yb.wealth.care.budget.repository.ExpenseBudgetRepository;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsBaseItemDto;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import com.yb.wealth.care.budget.resource.dto.ExpenseDetailItemUpsertDto;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static com.yb.wealth.care.budget.constant.ErrorMessages.ERROR_DUPLICATE_ENTRY;

@Slf4j
@RequiredArgsConstructor
@ApplicationScoped
public class BudgetDetailServiceImpl implements BudgetDetailsService {
    private final BudgetMapper budgetMapper;
    private final BudgetDetailsMapper budgetDetailsMapper;
    private final ExpenseBudgetDetailRepository expenseBudgetDetailRepository;
    private final ExpenseBudgetRepository expenseBudgetRepository;
    private final ExpenseCategories expenseCategories;

    @Override
    public Multi<BudgetExpenseDetailsItemDto> getBudgetDetails(final UUID budgetId) {
        log.info("INSIDE getBudgetDetails: {} ", budgetId);
        return expenseBudgetRepository.findByUserAndBudgetId(1, budgetId)
                .flatMap(budget -> expenseBudgetDetailRepository.getAllBudgetDetailsByBudgetId(budgetId))
                .onItem()
                .transformToMulti(list -> Multi.createFrom().iterable(list))
                .map(budgetDetailsMapper::toDto)
                .map(budgetExpenseDetail -> {
                    budgetExpenseDetail.setIcon(getCategoryIcon(budgetExpenseDetail.getCategory()));
                    return budgetExpenseDetail;
                })
                .onFailure()
                .transform(ExceptionHandler::handleGetError);
    }

    @Override
    @WithTransaction
    public Uni<Response> addBudgetDetailItem(final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                .map(expenseBudget -> checkForDuplicateExpenseItem(expenseBudget, expenseDetailItemUpsertDto))
                .map(expenseBudget -> {
                    validateExpenseCategory(expenseDetailItemUpsertDto.getCategory());
                    return expenseBudget;
                })
                .map(expenseBudget -> {
                    ExpenseBudgetDetail expenseBudgetDetail = budgetDetailsMapper.toExpenseBudgetDetail(expenseDetailItemUpsertDto);
                    expenseBudgetDetail.setId(UUID.randomUUID());
                    expenseBudgetDetail.setExpenseBudget(expenseBudget);
                    expenseBudget.getExpenseBudgetDetails().add(expenseBudgetDetail);
                    return expenseBudget;
                })
                .flatMap(expenseBudgetRepository::persist)
                .map(budgetMapper::toBudgetDto)
                .map(budgetDto -> {
                    budgetDto.getBudgetExpenseItems().forEach(item -> item.setIcon(getCategoryIcon(item.getCategory())));
                    return budgetDto;
                })
                .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                .onFailure(BadRequestException.class)
                .transform(ExceptionHandler::handleInsertError)
                .onFailure()
                .transform(err -> ExceptionHandler.handleInsertError(err, ErrorMessages.UNKNOWN_ERROR));
    }

    private void validateExpenseCategory(String expenseCategory) {
        if (expenseCategories.validate(expenseCategory)) {
            throw new BadRequestException(ErrorMessages.ERROR_INVALID_CATEGORY + expenseCategory);
        }
    }

    private ExpenseBudget checkForDuplicateExpenseItem(final ExpenseBudget expenseBudget,
                                                       final ExpenseDetailItemUpsertDto expenseBudgetDetail) {
        expenseBudget.getExpenseBudgetDetails().forEach(expenseBudgetDetailEntry -> {
            if (expenseBudgetDetailEntry.getExpenseName()
                    .equalsIgnoreCase(expenseBudgetDetail.getExpenseName())) {
                throw new BadRequestException(String.format(ERROR_DUPLICATE_ENTRY, expenseBudgetDetail.getExpenseName()));
            }
        });

        return expenseBudget;
    }

    private String getCategoryIcon(final String categoryName) {
       return expenseCategories.getExpenseCategoryByName(categoryName).getIcon();
    }

    @Override
    public Uni<Response> removeBudgetDetailItem(final UUID budgetDetailItemId) {
        return null;
    }

    @Override
    public Uni<Response> updateBudgetDetailItem(final UUID budgetDetailItemId,
                                                final BudgetExpenseDetailsBaseItemDto budgetDetailItem) {
        return null;
    }
}
