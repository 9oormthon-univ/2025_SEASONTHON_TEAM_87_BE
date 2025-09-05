package com.developing.bluffing.global.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );
        return ResponseEntity.badRequest().body(errors);
    }

    @ExceptionHandler(GlobalBaseException.class)
    public ResponseEntity handlePostException(GlobalBaseException exception) {
        GlobalException errorCode = exception.getErrorCode();

        return ResponseEntity.status(errorCode.getStatus())
                .body(commonExceptionResponse(exception.getErrorCode()));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity Exception(Exception e) {
        log.warn(e.getMessage());
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.INTERNAL_SERVER_ERROR);
        response.put("message", "서버 오류입니다.");

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(response);
    }

    private Map<String, Object> commonExceptionResponse(GlobalException globalException) {
        Map<String, Object> response = new HashMap<>();
        response.put("status", globalException.getStatus());
        response.put("errorCode", globalException.getErrorCode());
        response.put("message", globalException.getMessage());
        return response;
    }
}
