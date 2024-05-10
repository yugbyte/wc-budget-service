package com.yb.wealth.care.budget.repository;

import com.yb.wealth.care.budget.repository.entity.ExpenseBudget;
import io.quarkus.hibernate.reactive.panache.PanacheRepository;
import io.quarkus.hibernate.reactive.panache.common.WithTransaction;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.Cacheable;

import java.util.List;

@Cacheable
@ApplicationScoped
public class ExpenseBudgetRepository implements PanacheRepository<ExpenseBudget> {

    @WithTransaction
    public Uni<List<ExpenseBudget>> findWithPages(int pageIndex, int pageSize) {
        return findAll().page(pageIndex, pageSize)
                .list();
    }

}
