package com.yb.wealth.care.budget.exception;

import com.yb.wealth.care.budget.constant.ErrorMessages;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {

    public static Throwable handleUpsertError(Throwable error) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_BUDGET_NO_PERMISSION_NOT_EXIST);
        } else {
            log.error(ErrorMessages.ERROR_UPDATING_BUDGET, error);
            return new WebApplicationException(ErrorMessages.ERROR_CREATING_BUDGET);
        }
    }

    public static Throwable handleInsertError(Throwable error, String message) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_BUDGET_NO_PERMISSION_NOT_EXIST);
        } else if (error instanceof BadRequestException) {
            return error;
        } else {
            log.error(ErrorMessages.ERROR_UPDATING_BUDGET, error);
            return new WebApplicationException(message);
        }
    }

    public static Throwable handleInsertError(Throwable error) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_BUDGET_NO_PERMISSION_NOT_EXIST);
        } else if (error instanceof BadRequestException) {
            return error;
        } else {
            log.error(ErrorMessages.ERROR_UPDATING_BUDGET, error);
            return new WebApplicationException(error.getMessage());
        }
    }

    public static Throwable handleGetError(Throwable error) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_BUDGET_NO_PERMISSION_NOT_EXIST);
        } else {
            log.error("Unknown Error when getting budget: ", error);
            return new WebApplicationException(ErrorMessages.UNKNOWN_ERROR);
        }
    }
}
