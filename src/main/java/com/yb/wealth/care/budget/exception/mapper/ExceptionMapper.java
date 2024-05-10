package com.yb.wealth.care.budget.exception.mapper;

import com.yb.wealth.care.budget.exception.BadRequestException;
import com.yb.wealth.care.budget.exception.NotFoundException;
import com.yb.wealth.care.budget.resource.dto.ErrorDto;
import jakarta.ws.rs.core.Response;
import org.jboss.resteasy.reactive.RestResponse;
import org.jboss.resteasy.reactive.server.ServerExceptionMapper;

public class ExceptionMapper {

    @ServerExceptionMapper
    public RestResponse<ErrorDto> mapBadRequestException(BadRequestException x) {
        return RestResponse.status(Response.Status.BAD_REQUEST, ErrorDto.builder().message(x.getMessage()).build());
    }

    @ServerExceptionMapper
    public RestResponse<ErrorDto> mapNotFoundException(NotFoundException x) {
        return RestResponse.status(Response.Status.NOT_FOUND,  ErrorDto.builder().message(x.getMessage()).build());
    }
}
