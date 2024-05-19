package com.yb.wealth.care.budget.external.clients.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class ExpenseCategory {
    private String id;
    private String name;
    private String icon;
}
