package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ProductServiceImpl implements ProductService {
    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    
    public ProductServiceImpl(
        ProductRepository productRepository,
        ModelMapper modelMapper,
        CategoryRepository categoryRepository
    ) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public ProductDTO addProduct(Product product, Long categoryId) {
        Category category
            = categoryRepository
                  .findById(categoryId)
                  .orElseThrow(
                      () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
                  );
        
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() -
                                  ((product.getDiscount() * 0.01) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
}
