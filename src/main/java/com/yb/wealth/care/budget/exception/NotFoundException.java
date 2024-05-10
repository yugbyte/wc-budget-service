package com.yb.wealth.care.budget.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Getter
public class NotFoundException extends RuntimeException {
    String message;
}
