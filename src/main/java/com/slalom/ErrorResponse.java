package com.slalom;

public class ErrorResponse {
    private final String error;

    public ErrorResponse(String err) {
        error = err;
    }

    public String getError() {
        return error;
    }
}
