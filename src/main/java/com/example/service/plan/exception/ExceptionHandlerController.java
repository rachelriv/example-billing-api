package com.example.service.plan.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;

/**
 * Created by rrivera on 11/5/18.
 */
@RestControllerAdvice
public class ExceptionHandlerController {
    @ExceptionHandler(value = { Exception.class })
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ApiErrorResponse unknownException(Exception ex, WebRequest req) {
        ApiErrorResponse errorResponse = new ApiErrorResponse();
        errorResponse.setMessage(ex.getMessage());
        if (ex instanceof DataIntegrityViolationException) {
            errorResponse.setMessage("Data integrity violation " + ex.getMessage());
        }
        return errorResponse;
    }

}
