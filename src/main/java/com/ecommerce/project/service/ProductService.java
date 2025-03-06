package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(ProductDTO productDTO, Long categoryId);
    
    ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String orderBy);
    
    ProductResponse getProductsByCategory(Long categoryId, Integer pageNumber, Integer pageSize, String sortBy, String orderBy);
    
    ProductResponse getProductsByKeyWord(String keyword, Integer pageNumber, Integer pageSize, String sortBy, String orderBy);
    
    ProductDTO updateProduct(Long productId, ProductDTO productDTO);
    
    ProductDTO deleteProduct(Long productId);
    
    ProductDTO getProductById(Long productId);
}
