package com.login.GYMETRA.controller;

import com.login.GYMETRA.service.UserService.UserAlreadyExistsException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,Object>> onValidation(MethodArgumentNotValidException ex) {
        String msg = ex.getBindingResult().getAllErrors().get(0).getDefaultMessage();
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", msg));
    }

    @ExceptionHandler({UserAlreadyExistsException.class, IllegalArgumentException.class})
    public ResponseEntity<Map<String,Object>> onBadRequest(RuntimeException ex) {
        return ResponseEntity.badRequest().body(Map.of("success", false, "message", ex.getMessage()));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Map<String,Object>> onIntegrity(DataIntegrityViolationException ex) {
        return ResponseEntity.badRequest().body(Map.of(
                "success", false,
                "message", "Datos inválidos o duplicados (por ej., identificación/email único)."
        ));
    }
}
