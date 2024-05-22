package com.yb.wealth.care.budget.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Table(name = "expense_budget", schema = "budget")
@Entity
@ToString
@NamedQuery(name = "ExpenseBudget.deactivateCurrentBudgetForUser", query = "UPDATE ExpenseBudget e SET e.status=:newStatus WHERE e.userId= :userId AND e.status = :currentStatus")
public class ExpenseBudget {
    @Id
    @GeneratedValue
    public UUID id;
    private String name;
    private String status;
    @Column(name = "user_id")
    private Integer userId;
    private String tags;
    private String currency;
    @Column(name = "created_ts")
    private Instant createTime;
    @Column(name = "modified_ts")
    private Instant updatedTime;
    @OneToMany(mappedBy = "expenseBudget", cascade = CascadeType.ALL, orphanRemoval = true , fetch = FetchType.EAGER)
    private List<ExpenseBudgetDetail> expenseBudgetDetails;

}
