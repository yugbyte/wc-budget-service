package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.constant.ErrorMessages;
import com.yb.wealth.care.budget.constant.ExpenseCategories;
import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.exception.ExceptionHandler;
import com.yb.wealth.care.budget.exception.NotFoundException;
import com.yb.wealth.care.budget.mapper.BudgetDetailsMapper;
import com.yb.wealth.care.budget.mapper.BudgetMapper;
import com.yb.wealth.care.budget.repository.ExpenseBudgetDetailRepository;
import com.yb.wealth.care.budget.repository.ExpenseBudgetRepository;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import com.yb.wealth.care.budget.resource.dto.BudgetExpenseDetailsItemDto;
import com.yb.wealth.care.budget.resource.dto.ExpenseDetailItemUpsertDto;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import io.smallrye.mutiny.unchecked.Unchecked;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.util.UUID;

import static com.yb.wealth.care.budget.constant.ErrorMessages.CANNOT_DELETE_ITEM_DO_NOT_EXISTS_NO_PERMISSION;
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
                .transform(ExceptionHandler::handleError);
    }

    @Override
    @WithTransaction
    public Uni<Response> addBudgetDetailItem(final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                .map(expenseBudget -> this.validate(expenseBudget, expenseDetailItemUpsertDto))
                .map(expenseBudget -> prepareExpenseItemInsertData(expenseBudget, expenseDetailItemUpsertDto))
                .flatMap(expenseBudgetRepository::persist)
                .map(budgetMapper::toBudgetDto)
                .map(budgetDto -> {
                    budgetDto.getBudgetExpenseItems().forEach(item -> item.setIcon(getCategoryIcon(item.getCategory())));
                    return budgetDto;
                })
                .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                .onFailure(BadRequestException.class)
                .transform(ExceptionHandler::handleError)
                .onFailure()
                .transform(err -> ExceptionHandler.handleError(err, ErrorMessages.UNKNOWN_ERROR));
    }

    private void validateExpenseCategory(String expenseCategory) {
        if (expenseCategories.validate(expenseCategory)) {
            throw new BadRequestException(ErrorMessages.ERROR_INVALID_CATEGORY + expenseCategory);
        }
    }

    private void checkForDuplicateExpenseItem(final ExpenseBudget expenseBudget,
                                              final ExpenseDetailItemUpsertDto expenseBudgetDetail) {
        expenseBudget.getExpenseBudgetDetails().forEach(expenseBudgetDetailEntry -> {
            if (expenseBudgetDetailEntry.getExpenseName()
                    .equalsIgnoreCase(expenseBudgetDetail.getExpenseName())) {
                throw new BadRequestException(String.format(ERROR_DUPLICATE_ENTRY, expenseBudgetDetail.getExpenseName()));
            }
        });
    }

    private String getCategoryIcon(final String categoryName) {
       return expenseCategories.getExpenseCategoryByName(categoryName).getIcon();
    }

    @Override
    @WithTransaction
    public Uni<Response> removeBudgetDetailItem(final UUID budgetDetailItemId) {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                    .onItem()
                    .ifNull()
                    .failWith(() ->  new NotFoundException(CANNOT_DELETE_ITEM_DO_NOT_EXISTS_NO_PERMISSION))
                    .map(Unchecked.function(expenseBudget -> {
                        log.debug("INSIDE removeBudgetDetailItem: {} ", budgetDetailItemId);
                        int initialSize = expenseBudget.getExpenseBudgetDetails().size();
                        log.debug("Initial expense details size: {}", initialSize);
                        expenseBudget.getExpenseBudgetDetails().removeIf(expenseBudgetDetailItem -> expenseBudgetDetailItem.getId().equals(budgetDetailItemId));
                        log.debug("Final expense details size: {}", expenseBudget.getExpenseBudgetDetails().size());
                        if (initialSize == expenseBudget.getExpenseBudgetDetails().size()) {
                            throw new NotFoundException(CANNOT_DELETE_ITEM_DO_NOT_EXISTS_NO_PERMISSION);
                        }
                        return expenseBudget;
                    }))
                    .flatMap(expenseBudgetRepository::persist)
                    .map(budgetMapper::toBudgetDto)
                    .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                    .onFailure()
                    .transform(ExceptionHandler::handleError);
    }

    @Override
    @WithTransaction
    public Uni<Response> updateBudgetDetailItem(final UUID budgetDetailItemId,
                                                final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        return expenseBudgetRepository.findActiveBudgetForUser(1)
                .onItem()
                .ifNull()
                .failWith(() ->  new NotFoundException(ErrorMessages.ERROR_NO_PERMISSION_NOT_EXIST))
                .map(expenseBudget -> {
                    validateExpenseCategory(expenseDetailItemUpsertDto.getCategory());
                    return expenseBudget;
                })
                .map(expenseBudget -> {
                    ExpenseBudgetDetail expenseBudgetDetailExisting = expenseBudget.getExpenseBudgetDetails()
                            .stream()
                            .filter(expenseBudgetDetail -> expenseBudgetDetail.getId().equals(budgetDetailItemId) &&
                                    expenseBudgetDetail.getExpenseName().equalsIgnoreCase(expenseDetailItemUpsertDto.getExpenseName()))
                            .findFirst()
                            .orElseThrow(() -> new NotFoundException(ErrorMessages.ERROR_NO_PERMISSION_NOT_EXIST));
                    setNewBudgetDetails(expenseBudgetDetailExisting, expenseDetailItemUpsertDto);
                    return expenseBudget;
                })
                .flatMap(expenseBudgetRepository::persist)
                .map(budgetMapper::toBudgetDto)
                .flatMap(expenseBudget -> Uni.createFrom().item(Response.status(Response.Status.OK).entity(expenseBudget).build()))
                .onFailure()
                .transform(ExceptionHandler::handleError);
    }

    private ExpenseBudget validate(final ExpenseBudget expenseBudget, final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        checkForDuplicateExpenseItem(expenseBudget, expenseDetailItemUpsertDto);
        validateExpenseCategory(expenseDetailItemUpsertDto.getCategory());
        return expenseBudget;
    }

    private void setNewBudgetDetails(final ExpenseBudgetDetail existingExpenseBudgetDetail,
                                                   final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        ExpenseBudgetDetail expenseBudgetDetailNew = budgetDetailsMapper.toExpenseBudgetDetail(expenseDetailItemUpsertDto);
        if (StringUtils.isNotBlank(expenseBudgetDetailNew.getExpenseName())) {
            existingExpenseBudgetDetail.setExpenseName(expenseBudgetDetailNew.getExpenseName());
        }
        if (expenseBudgetDetailNew.getBudgetAmount().compareTo(BigDecimal.ONE) > 0) {
            existingExpenseBudgetDetail.setBudgetAmount(expenseBudgetDetailNew.getBudgetAmount());
        }
        if (StringUtils.isNotBlank(expenseBudgetDetailNew.getCategory())) {
            existingExpenseBudgetDetail.setCategory(expenseBudgetDetailNew.getCategory());
        }
        if (expenseBudgetDetailNew.getCategory() != null) {
            existingExpenseBudgetDetail.setIsRecurring(expenseBudgetDetailNew.getIsRecurring());
        }
        if (expenseBudgetDetailNew.getTags() != null) {
            existingExpenseBudgetDetail.setTags(expenseBudgetDetailNew.getTags());
        }
    }

    private ExpenseBudget prepareExpenseItemInsertData(final ExpenseBudget expenseBudget,
                                      final ExpenseDetailItemUpsertDto expenseDetailItemUpsertDto) {
        ExpenseBudgetDetail expenseBudgetDetail = budgetDetailsMapper.toExpenseBudgetDetail(expenseDetailItemUpsertDto);
        expenseBudgetDetail.setId(UUID.randomUUID());
        expenseBudgetDetail.setExpenseBudget(expenseBudget);
        expenseBudget.getExpenseBudgetDetails().add(expenseBudgetDetail);
        return expenseBudget;
    }
}
