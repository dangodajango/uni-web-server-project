package com.example.demo.common.web;

import com.example.demo.common.exception.RequestValidationException;
import com.example.demo.common.exception.ResourceNotFoundException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import static org.springframework.http.HttpStatus.*;

@ControllerAdvice
public class ControllerExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<HttpErrorResponse> handleResourceNotFoundException(ResourceNotFoundException resourceNotFoundException) {
        return new ResponseEntity<>(new HttpErrorResponse(resourceNotFoundException.getMessage()), NOT_FOUND);
    }

    @ExceptionHandler(RequestValidationException.class)
    public ResponseEntity<HttpErrorResponse> handleRequestValidationException(RequestValidationException requestValidationException) {
        return new ResponseEntity<>(new HttpErrorResponse(requestValidationException.getMessage()), BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<HttpErrorResponse> handleException(Exception exception) {
        return new ResponseEntity<>(new HttpErrorResponse(exception.getMessage()), INTERNAL_SERVER_ERROR);
    }
}
