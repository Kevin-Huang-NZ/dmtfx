package com.mahara.stocker.util.validate;

import javafx.scene.Node;

import java.util.function.Function;

public class ValidateRule<T> {
    private Function<T, Boolean> validateFunction;
    private String errorMessage;
    
    public ValidateRule(Function<T, Boolean> validateFunction, String errorMessage) {
        this.validateFunction = validateFunction;
        this.errorMessage = errorMessage;
    }

    public Function<T, Boolean> getValidateFunction() {
        return validateFunction;
    }

    public void setValidateFunction(Function<T, Boolean> validateFunction) {
        this.validateFunction = validateFunction;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
