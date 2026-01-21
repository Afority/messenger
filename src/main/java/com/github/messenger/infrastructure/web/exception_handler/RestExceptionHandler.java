package com.github.messenger.infrastructure.web.exception_handler;

import com.github.messenger.domain.exceptions.*;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class RestExceptionHandler {
    @ExceptionHandler(LoginIsInvalidException.class)
    public ResponseEntity<String> handleLoginIsInvalidException(LoginIsInvalidException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(PasswordIsWeakException.class)
    public ResponseEntity<String> handlePasswordIsWeakException(PasswordIsWeakException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UserAlreadyExistsException.class)
    public ResponseEntity<String> handleUserAlreadyExistsException(UserAlreadyExistsException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFoundException(UserNotFoundException e) {
        return ResponseEntity.badRequest().body(e.getMessage());
    }

// template
//    @ExceptionHandler(.class)
//    public ResponseEntity<String> handle( e) {
//        return ResponseEntity.badRequest().body(e.getMessage());
//    }
}
