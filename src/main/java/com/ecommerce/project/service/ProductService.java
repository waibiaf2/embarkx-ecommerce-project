package com.ecommerce.project.service;

import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;

public interface ProductService {
    ProductDTO addProduct(Product product, Long categoryId);
    ProductResponse getAllProducts(Integer pageNumber,Integer pageSize, String sortBy, String orderBy);
    ProductResponse getProductsByCategory(Long categoryId);
    ProductResponse getProductsByKeyWord(String keyword);
    ProductDTO updateProduct(Long productId, Product product);
    ProductDTO deleteProduct(Long productId);
    ProductDTO getProductById(Long productId);
}
