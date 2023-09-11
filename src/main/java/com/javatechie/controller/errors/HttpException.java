package com.javatechie.controller.errors;

import org.springframework.http.HttpStatus;

public class HttpException extends RuntimeException {
    private HttpStatus status;

    public HttpException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}