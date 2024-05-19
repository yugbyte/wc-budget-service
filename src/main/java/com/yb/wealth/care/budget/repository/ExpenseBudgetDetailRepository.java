package com.yb.wealth.care.budget.repository;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudgetDetail;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Cacheable;

import java.util.List;
import java.util.UUID;

@Cacheable
@ApplicationScoped
public class ExpenseBudgetDetailRepository implements PanacheRepository<ExpenseBudgetDetail> {

    @WithTransaction
    public Uni<List<ExpenseBudgetDetail>> getAllBudgetDetailsByBudgetId(final UUID budgetId) {
        return list("expenseBudget.id", budgetId);
    }
}
