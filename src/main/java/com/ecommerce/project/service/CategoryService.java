package com.ecommerce.project.service;

import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;

public interface CategoryService  {
    CategoryDTO  getCategory(Long categoryId);
    CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder);
    CategoryDTO createCategory(CategoryDTO category);
    CategoryDTO deleteCategory(Long categoryId);
    CategoryDTO updateCategory(Long id, CategoryDTO categoryDTO);
}
