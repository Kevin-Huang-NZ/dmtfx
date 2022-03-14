package com.mahara.stocker.util;

public class ValidationException extends RuntimeException{
    public ValidationException() {
        super();
    }
    public ValidationException(String errorMessage) {
        super(errorMessage);
    }
}
