package com.ecommerce.project.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.NumberFormat;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long productId;
    
    @NotBlank(message = "Product name is required")
    @Size(min = 3, message = "Product name must be at least 5 characters")
    private String productName;
    
    @Size(min = 6, message = "Product name must be at least 5 characters")
    private String description;
    
    @Min(value = 0, message = "Price must be grater or equal to 0")
    @NumberFormat(style = NumberFormat.Style.CURRENCY)
    private Double price;
    
    private String image;
    
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;
    
    private Double specialPrice;
    
    @Min(value = 0, message = "Discount must be grater or equal to 0")
    private Double discount = 0.0;
    
    @ManyToOne(cascade = {CascadeType.REFRESH, CascadeType.MERGE}, optional = false)
    @JoinColumn(name = "category_id")
    private Category category;
}
