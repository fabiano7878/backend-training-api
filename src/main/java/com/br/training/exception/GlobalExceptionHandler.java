package com.br.training.exception;

import java.time.Instant;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
	 private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	
    @ExceptionHandler(FileStorageException.class)
    public ResponseEntity<Map<String, Object>> handleFileError(FileStorageException ex) {

        Map<String, Object> body = new HashMap<>();
        body.put("error", ex.getMessage());
        body.put("timestamp", Instant.now());

        return ResponseEntity.badRequest().body(body);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleGeneric(Exception ex) {
   	
        Map<String, Object> body = new HashMap<>();
        log.error("Erro interno na API: ", ex);
        body.put("error", "Erro interno na API, contate o administrador do sistema.");
        body.put("timestamp", Instant.now());

        return ResponseEntity.status(500).body(body);
    }
}
