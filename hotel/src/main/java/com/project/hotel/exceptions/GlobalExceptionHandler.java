package com.project.hotel.exceptions;


import com.project.hotel.exceptions.custom.ConflictException;
import com.project.hotel.exceptions.custom.ResourceNotFoundException;
import com.project.hotel.exceptions.custom.ServiceUnavailableException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.stream.Collectors;


@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiError> handleNotfound(ResourceNotFoundException ex){
        log.error("Resource not found: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ApiError(
                        404,
                        "Not Found",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }


    // 400 — bad request
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex) {
        log.error("Bad request: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        400,
                        "Bad Request",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    // 400 — validation errors (@Valid failures)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(err -> err.getField() + ": " + err.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.error("Validation failed: {}", message);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ApiError(
                        400,
                        "Validation Failed",
                        message,
                        LocalDateTime.now()
                ));
    }

    // 409 — conflict (duplicate booking etc)
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ApiError> handleConflict(ConflictException ex) {
        log.error("Conflict: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(new ApiError(
                        409,
                        "Conflict",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    // 503 — downstream service unavailable (Feign errors)
    @ExceptionHandler(ServiceUnavailableException.class)
    public ResponseEntity<ApiError> handleServiceUnavailable(ServiceUnavailableException ex) {
        log.error("Service unavailable: {}", ex.getMessage());
        return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(new ApiError(
                        503,
                        "Service Unavailable",
                        ex.getMessage(),
                        LocalDateTime.now()
                ));
    }

    // 500 — catch all
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiError> handleGeneral(Exception ex) {
        log.error("Unexpected error: {}", ex.getMessage(), ex);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ApiError(
                        500,
                        "Internal Server Error",
                        "Something went wrong",
                        LocalDateTime.now()
                ));
    }


}
