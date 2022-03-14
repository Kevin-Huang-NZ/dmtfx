package com.mahara.stocker.util.validate;

public class ValidateResult {
    private boolean success = true;
    private String errorMessage = null;

    public ValidateResult() {
    }

    public ValidateResult(String msg) {
        this();
        success = false;
        errorMessage = msg;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }
}
