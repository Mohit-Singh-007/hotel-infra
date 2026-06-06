package com.project.booking.exceptions.custom;

public class ConflictException extends RuntimeException {
    public ConflictException(String message) {
        super(message);
    }
}
