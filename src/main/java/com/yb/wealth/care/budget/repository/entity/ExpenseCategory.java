package com.yb.wealth.care.budget.repository.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Table(name = "expense_category", schema = "budget")
@Entity
public class ExpenseCategory {
    @Id
    private UUID id;
    @Column(name = "category_name")
    private String categoryName;
    private String icon;
}
