package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    
    public CategoryServiceImpl(CategoryRepository categoryRepository) {
        this.categoryRepository = categoryRepository;
    }
    
    @Override
    public void createCategory(Category category) {
        categoryRepository.save(category);
    }
    
    @Override
    public Category getCategory(Long categoryId) {
        Category category
            = categoryRepository.findById(categoryId)
                  .orElseThrow(() ->  new ResourceNotFoundException("Category", "categoryId", categoryId));
        
        return category;
    }
    
    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public String deleteCategory(Long categoryId) {
        Category categoryToDelete
            = categoryRepository.findById(categoryId)
                  .orElseThrow(() ->  new ResourceNotFoundException("Category", "categoryId", categoryId));
        categoryRepository.delete(categoryToDelete);
        
        return "Category with categoryId " + categoryId + " deleted successfully";
    }
    
    @Override
    public Category updateCategory(Long categoryId, Category category) {
        Category categoryToUpdate
            = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        
        categoryToUpdate.setCategoryName(category.getCategoryName());
        categoryRepository.save(categoryToUpdate);
        
        return categoryToUpdate;
    }
}
