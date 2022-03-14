package com.mahara.stocker.util.task;

public class TaskResult {
    private boolean success = true;
    private Throwable t;
    private String message;

    public TaskResult() {
    }

    public TaskResult(Throwable t) {
        this(t, null);
    }

    public TaskResult(Throwable t, String message) {
        success = false;
        this.t = t;
        this.message = message;
    }

    public boolean isSuccess() {
        return success;
    }

    public void setSuccess(boolean success) {
        this.success = success;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Throwable getT() {
        return t;
    }

    public void setT(Throwable t) {
        this.t = t;
    }
}
