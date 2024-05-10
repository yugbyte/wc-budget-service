package com.yb.wealth.care.budget.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class BadRequestException extends RuntimeException {
    private String message;
}
