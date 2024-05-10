package com.yb.wealth.care.budget.service;

import com.yb.wealth.care.budget.mapper.BudgetDetailsMapper;
import com.yb.wealth.care.budget.mapper.BudgetMapper;
import com.yb.wealth.care.budget.repository.ExpenseBudgetDetailRepository;
import com.yb.wealth.care.budget.repository.ExpenseBudgetRepository;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsBaseDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDetailsDto;
import com.yb.wealth.care.budget.resource.dto.BudgetDto;
import io.smallrye.mutiny.Multi;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@RequiredArgsConstructor
@ApplicationScoped
@Slf4j
public class BudgetServiceImpl implements BudgetService {
    private final ExpenseBudgetRepository expenseBudgetRepository;
    private final ExpenseBudgetDetailRepository expenseBudgetDetailRepository;
    private final BudgetMapper budgetMapper;
    private final BudgetDetailsMapper budgetDetailsMapper;

    @Override
    public Multi<BudgetDto> getBudgets(int page, int limit) {
        log.debug("INSIDE getBudgets: {} {}", page, limit);
        return expenseBudgetRepository.findWithPages(page, limit)
                .onItem()
                .transformToMulti(item -> Multi.createFrom().iterable(item))
                .map(budgetMapper::toBudget);
    }

    @Override
    public Multi<BudgetDetailsDto> getBudgetDetails(UUID budgetId) {
        return expenseBudgetDetailRepository.getAllBudgetDetailsByBudgetId(budgetId)
                .onItem()
                .transformToMulti(list -> Multi.createFrom().iterable(list))
                .map(budgetDetailsMapper::toDto);
    }

    @Override
    public Uni<Void> createBudget(BudgetDetailsBaseDto budgetDetails) {
        return null;
    }
}
