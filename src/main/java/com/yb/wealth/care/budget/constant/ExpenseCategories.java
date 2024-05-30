package com.yb.wealth.care.budget.constant;

import com.yb.wealth.care.budget.external.clients.WcUiServiceClient;
import com.yb.wealth.care.budget.external.clients.dto.ExpenseCategory;
import io.quarkus.runtime.Startup;
import io.smallrye.common.annotation.Blocking;
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@ApplicationScoped
@RequiredArgsConstructor
@Slf4j
public class ExpenseCategories {
    private final Map<String, ExpenseCategory> expenseCategoryMap = new HashMap<>();
    private final ExpenseCategory defaultExpenseCategory = new ExpenseCategory();
    private Instant lastUpdateTime = Instant.now();

    @ConfigProperty(name = "expense.category.cache.data.refresh.interval.minutes")
    int refreshInterval;
    @RestClient
    @Inject
    WcUiServiceClient wcUiServiceClient;

    @Startup
    public void onInit() {
        defaultExpenseCategory.setIcon("defaultIcon");
        defaultExpenseCategory.setName("defaultName");
        refreshDataIfNeeded();
    }

    public boolean validate(final String name) {
        refreshDataIfNeeded();
        return expenseCategoryMap.containsKey(name);
    }

    public ExpenseCategory getExpenseCategoryByName(final String name) {
        refreshDataIfNeeded();
        final ExpenseCategory expenseCategory = expenseCategoryMap.get(name);
        return null == expenseCategory ? this.defaultExpenseCategory : expenseCategory;
    }

    private void refreshDataIfNeeded() {
        if (expenseCategoryMap.isEmpty() || isDataExpired()) {
            log.debug("Initiating loading category data");
           lastUpdateTime = loadData();
           log.debug("Expense Category Data Refreshed");
        }
    }

    private boolean isDataExpired() {
       return Duration.between(lastUpdateTime, Instant.now()).toMinutes() > refreshInterval;
    }

    private Instant loadData() {
        log.debug("Inside loadData()");
        wcUiServiceClient.getExpenseCategories()
                .subscribe()
                .with(expenseCategories -> {
                    expenseCategories.forEach( expenseCategory ->
                        expenseCategoryMap.put(expenseCategory.getName(), expenseCategory)
                    );
                 });
        return Instant.now();
    }
}
