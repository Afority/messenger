package com.github.messenger.infrastructure.web.exception_handler;

import com.github.messenger.domain.exceptions.*;
import com.github.messenger.infrastructure.web.response.ErrorResponse;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorResponse> domainException(DomainException e) {
        ErrorResponse response = new ErrorResponse(e.getMessage());

        return ResponseEntity.badRequest().body(response);
    }
}
