package com.yb.wealth.care.budget.repository.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table(name = "expense_budget", schema = "budget")
@Entity
public class ExpenseBudget {
    @Id
    @GeneratedValue
    private UUID id;
    private String name;
    private String status;
    @Column(name = "user_id")
    private Integer userId;
    private String tags;
    private String currency;
}
