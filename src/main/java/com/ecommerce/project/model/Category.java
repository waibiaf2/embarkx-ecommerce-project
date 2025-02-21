package com.ecommerce.project.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;

@Entity(name = "categories")
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long categoryId;
    
    @NotBlank(message = "categoryName must not be blank")
    @Size(min = 5, message = "categoryName must contain be at least 5 characters")
    private String categoryName;
}

