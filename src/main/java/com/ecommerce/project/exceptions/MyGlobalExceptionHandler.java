package com.ecommerce.project.exceptions;

import com.ecommerce.project.payload.APIResponse;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class MyGlobalExceptionHandler {
    
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, String>> myMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> response = new HashMap<>();
        
        ex.getBindingResult().getAllErrors().forEach((err) -> {
            String fieldName = ((FieldError) err).getField();
            String errorMessage = err.getDefaultMessage();
            response.put(fieldName, errorMessage);
        });
        
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }
    
    @ExceptionHandler(ConstraintViolationException.class)
    public ResponseEntity<Map<String, String>> myConstraintViolationException(ConstraintViolationException ex) {
        Map<String, String> response = new HashMap<>();
        
        ex.getConstraintViolations().forEach((err) -> {
            String fieldName = err.getPropertyPath().toString();
            String errorMessage = err.getMessage();
            response.put(fieldName, errorMessage);
        });
        
        //return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<APIResponse> resourceNotFoundException(ResourceNotFoundException e) {
        String errorMessage = e.getMessage();
        APIResponse apiResponse = new APIResponse(errorMessage,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(APIException.class)
    public ResponseEntity<APIResponse> apiException(APIException e) {
        String errorMessage = e.getMessage();
        APIResponse apiResponse = new APIResponse(errorMessage,false);
        return new ResponseEntity<>(apiResponse, HttpStatus.BAD_REQUEST);
    }
}
