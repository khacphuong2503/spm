package com.javatechie.controller.errors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        String errorMessage = ex.getBindingResult().getFieldErrors().get(0).getDefaultMessage();

        // Xử lý lỗi hợp lệ không thành công theo ý muốn (ví dụ: trả về thông báo lỗi)

        return ResponseEntity.badRequest().body(errorMessage);
    }

    // Các phương thức xử lý ngoại lệ khác

    // ...
}
