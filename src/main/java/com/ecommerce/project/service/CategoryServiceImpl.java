package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoryServiceImpl implements CategoryService {
    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;
    
    public CategoryServiceImpl(
        CategoryRepository categoryRepository,
        ModelMapper modelMapper
    ) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }
    
    @Override
    public CategoryDTO createCategory(CategoryDTO categoryDTO) {
        Category category = modelMapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.findByName(category.getName());
        
        if (savedCategory != null) {
            throw new APIException(
                "A category with the same name " + categoryDTO.getName() + " already exists"
            );
        }
        
        categoryRepository.save(category);
        
        return modelMapper.map(category, CategoryDTO.class);
    }
    
    @Override
    public Category getCategory(Long id) {
        Category category
            = categoryRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        return category;
    }
    
    @Override
    public CategoryResponse getCategories() {
        List<Category> categories = categoryRepository.findAll();
        
        if (categories.isEmpty())
            throw new APIException("No categories have been create yet!");
        
        List<CategoryDTO> categoryDTOS
            = categories.stream()
                  .map(category -> modelMapper.map(category, CategoryDTO.class))
                  .toList();
        
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        
        return categoryResponse;
    }
    
    
    @Override
    public CategoryDTO deleteCategory(Long id) {
        Category categoryToDelete
            = categoryRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", id));
        categoryRepository.delete(categoryToDelete);
        
        return modelMapper.map(categoryToDelete, CategoryDTO.class);
    }
    
    @Override
    public CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO) {
        Category categoryToUpdate
            = categoryRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", id));
        
        Category category = modelMapper.map(categoryDTO, Category.class);
        categoryToUpdate.setName(category.getName());
        categoryRepository.save(categoryToUpdate);
        
        return modelMapper.map(categoryToUpdate, CategoryDTO.class);
    }
}
