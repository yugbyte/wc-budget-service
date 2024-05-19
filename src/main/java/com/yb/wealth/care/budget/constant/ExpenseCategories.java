package com.yb.wealth.care.budget.constant;

import com.yb.wealth.care.budget.external.clients.WcUiServiceClient;
import com.yb.wealth.care.budget.external.clients.dto.ExpenseCategory;
import io.quarkus.runtime.Startup;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.microprofile.config.inject.ConfigProperty;
import org.eclipse.microprofile.rest.client.inject.RestClient;

import java.time.Duration;
import java.time.Instant;
import java.util.HashMap;
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
        log.info("INIT CATEGORY DATA ");
        defaultExpenseCategory.setIcon("defaultIcon");
        defaultExpenseCategory.setName("defaultName");
        loadData();
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
            lastUpdateTime = loadData().join();
            log.debug("Expense Category Data Refreshed");
        }
    }

    private boolean isDataExpired() {
       return Duration.between(lastUpdateTime, Instant.now()).toMinutes() > refreshInterval;
    }

    private CompletableFuture<Instant> loadData() {
        final CompletableFuture<Instant> future = new CompletableFuture<>();
        wcUiServiceClient.getExpenseCategories()
                .onFailure()
                .retry()
                .atMost(3)
                .subscribe()
                .with(expenseCategoryList -> {
                     expenseCategoryList
                             .forEach(expenseCategory -> expenseCategoryMap.put(expenseCategory.getName(), expenseCategory));
                 },
                 failure-> {
                     log.error("Error while getting expense category", failure);
                     future.complete(Instant.now());
                 }, () -> {
                    log.debug("Filled expense categories map");
                    future.complete(Instant.now());
                 });
        return future;
    }
}
