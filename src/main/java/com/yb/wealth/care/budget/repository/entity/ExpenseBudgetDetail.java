package com.yb.wealth.care.budget.repository.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@Table(name = "expense_budget_detail", schema = "budget")
@Entity
public class ExpenseBudgetDetail {
    @Id
    private UUID id;
    @Column(name = "expense_name")
    private String expenseName;
    @Column(name = "budget_amount")
    private BigDecimal budgetAmount;
    @Column(name = "is_recurring")
    private Boolean isRecurring;
    @Column(name = "created_ts")
    private Instant createdTime;
    @Column(name = "modified_ts")
    private Instant modifiedTime;
    private String tags;
    @Column(name = "category")
    private String category;
    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "expense_budget_id")
    private ExpenseBudget expenseBudget;
}
