package com.yb.wealth.care.budget.repository;

import com.yb.wealth.care.budget.constant.BudgetStatus;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Cacheable;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.UUID;

@Cacheable
@ApplicationScoped
@Slf4j
public class ExpenseBudgetRepository implements PanacheRepository<ExpenseBudget> {

    @WithTransaction
    public Uni<List<ExpenseBudget>> findWithPages(final int pageIndex, final int pageSize) {
        return findAll().page(pageIndex, pageSize)
                .list();
    }

    @WithTransaction
    public Uni<ExpenseBudget> findByUserAndBudgetId(final int userId, final UUID budgetId) {
        return find("userId = :userId and id = :budgetId",
                Parameters.with("userId", userId).and("budgetId", budgetId).map())
                .singleResult();
    }

    @WithTransaction
    public Uni<ExpenseBudget> findActiveBudgetForUser(final int userId) {
        return find("userId = :userId and status = :currentStatus",
                Parameters.with("userId", userId).and("currentStatus", BudgetStatus.A.name()).map())
                .singleResult();
    }

    @WithTransaction
    public Uni<ExpenseBudget> findBudgetForUserWithBudgetName(final int userId, final String budgetName) {
        return find("userId = :userId and name = :budgetName",
                Parameters.with("userId", userId).and("budgetName", budgetName).map())
                .singleResult();
    }

    public Uni<Boolean> deactivateCurrentBudgetForUser(final int userId) {
        log.info("De-Activating current budget for user {}", userId);
        return update("#ExpenseBudget.deactivateCurrentBudgetForUser", Parameters.with("userId", userId)
                        .and("newStatus", BudgetStatus.I.name())
                        .and("currentStatus", BudgetStatus.A.name())
                ).onItem()
                .transform(status -> status > 0);
    }

}
