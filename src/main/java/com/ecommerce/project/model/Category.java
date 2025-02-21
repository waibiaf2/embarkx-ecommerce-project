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
    
    @NotBlank(message = "Validation is a required field.")
    @Size(min = 3, max = 50, message = "Category name must have a minimum of 3 characters and a maximum of 50 characters.")
    private String categoryName;
}

