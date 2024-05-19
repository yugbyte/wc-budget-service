package com.yb.wealth.care.budget.exception.mapper;

import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.exception.NotFoundException;
import com.yb.wealth.care.budget.resource.dto.ErrorDto;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

import java.util.ArrayList;
import java.util.List;


@Slf4j
public class ExceptionMapper {

    @ServerExceptionMapper
    public RestResponse<ErrorDto> mapBadRequestException(BadRequestException x) {
        List<String> errors = new ArrayList<>();
        errors.add(x.getMessage());
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorDto.builder().messages(errors).build());
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> mapNotFoundException(NotFoundException x) {
        List<String> errors = new ArrayList<>();
        errors.add(x.getMessage());
        return RestResponse.status(Response.Status.FORBIDDEN,  ErrorDto.builder().messages(errors).build());
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> constraintException(ConstraintViolationException x) {
        List<String> errors = new ArrayList<>();
        x.getConstraintViolations().forEach(message -> errors.add(message.getMessage()));
        return RestResponse.status(Response.Status.BAD_REQUEST,  ErrorDto.builder().messages(errors).build());
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> internalServerError(WebApplicationException x) {
        List<String> errors = new ArrayList<>();
        errors.add(x.getMessage());
        return RestResponse.status(Response.Status.INTERNAL_SERVER_ERROR,  ErrorDto.builder().messages(errors).build());
    }
}
