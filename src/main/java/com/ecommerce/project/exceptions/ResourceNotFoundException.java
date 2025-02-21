package com.ecommerce.project.exceptions;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
public class ResourceNotFoundException extends RuntimeException {
    String resourceName;
    String field;
    String fieldName;
    Long fieldId;
    
    public ResourceNotFoundException(String message, String resourceName, String field, String fieldName) {
        super(String.format("%s not found %s: %s", resourceName, fieldName, message));
        this.resourceName = resourceName;
        this.field = field;
        this.fieldName = fieldName;
    }
    
    public ResourceNotFoundException(String field, String resourceName, Long fieldId) {
        super(String.format("%s not found %s: %d", resourceName, field, fieldId));
        this.field = field;
        this.resourceName = resourceName;
        this.fieldId = fieldId;
    }
}
