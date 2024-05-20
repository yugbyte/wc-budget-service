package com.yb.wealth.care.budget.exception;

import com.yb.wealth.care.budget.constant.ErrorMessages;
import jakarta.persistence.NoResultException;
import jakarta.ws.rs.WebApplicationException;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ExceptionHandler {

    public static Throwable handleError(Throwable error, String message) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_NO_PERMISSION_NOT_EXIST);
        } else  if (error instanceof NotFoundException) {
            return error;
        }  else if (error instanceof BadRequestException) {
            return error;
        } else {
            log.error("Error Occurred: {}", error.getMessage(), error);
            return new WebApplicationException(message);
        }
    }

    public static Throwable handleError(Throwable error) {
        if (error instanceof NoResultException) {
            return new NotFoundException(ErrorMessages.ERROR_NO_PERMISSION_NOT_EXIST);
        } else  if (error instanceof NotFoundException) {
            return error;
        }  else if (error instanceof BadRequestException) {
            return error;
        } else {
            log.error("Error Occurred: {}", error.getMessage(), error);
            return new WebApplicationException(error.getMessage());
        }
    }
}
