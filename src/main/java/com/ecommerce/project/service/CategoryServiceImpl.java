package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.repositories.CategoryRepository;
import org.springframework.stereotype.Service;

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
    public Category getCategory(Long id) {
        Category category
            = categoryRepository.findById(id)
                  .orElseThrow(() ->  new ResourceNotFoundException("Category", "id", id));
        
        return category;
    }
    
    @Override
    public List<Category> getCategories() {
        return categoryRepository.findAll();
    }
    
    @Override
    public String deleteCategory(Long id) {
        Category categoryToDelete
            = categoryRepository.findById(id)
                  .orElseThrow(() ->  new ResourceNotFoundException("Category", "categoryId", id));
        categoryRepository.delete(categoryToDelete);
        
        return "Category with categoryId " + id + " deleted successfully";
    }
    
    @Override
    public Category updateCategory(Long id, Category category) {
        Category categoryToUpdate
            = categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", id));
        
        categoryToUpdate.setName(category.getName());
        categoryRepository.save(categoryToUpdate);
        
        return categoryToUpdate;
    }
}
