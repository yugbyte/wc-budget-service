package com.yb.wealth.care.budget.repository;

import com.yb.wealth.care.budget.constant.BudgetStatus;
import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.quarkus.panache.common.Parameters;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Cacheable;

import java.util.List;
import java.util.UUID;

@Cacheable
@ApplicationScoped
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



    public Uni<Boolean> deactivateCurrentBudget() {
        return update("#ExpenseBudget.deactivateCurrentBudget", Parameters.with("userId", 1)
                        .and("newStatus", BudgetStatus.I.name())
                        .and("currentStatus", BudgetStatus.A.name())
                )
                .onItem()
                .transform(status ->  status > 0);
    }

}
