package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.payload.CategoryDTO;
import com.ecommerce.project.payload.CategoryResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
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
        Category savedCategory = categoryRepository.findByCategoryName(category.getCategoryName());
        
        if (savedCategory != null) {
            throw new APIException(
                "A category with the same name " + categoryDTO.getCategoryName() + " already exists"
            );
        }
        
        categoryRepository.save(category);
        
        return modelMapper.map(category, CategoryDTO.class);
    }
    
    @Override
    public CategoryDTO getCategory(Long id) {
        Category category
            = categoryRepository.findById(id)
                  .orElseThrow(() -> new ResourceNotFoundException("Category", "id", id));
        
        return modelMapper.map(category, CategoryDTO.class);
    }
    
    @Override
    public CategoryResponse getAllCategories(Integer pageNumber, Integer pageSize, String sortBy, String sortOrder) {
        Sort sortByAndOrder = sortOrder.equalsIgnoreCase("asc") ?
                                  Sort.by(sortBy).ascending() :
                                  Sort.by(sortBy).descending();
        
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortByAndOrder);
        Page<Category> categoryPage = categoryRepository.findAll(pageDetails);
        List<Category> categories = categoryPage.getContent();
        
        if(categoryPage.isEmpty())
            throw new APIException("No more categories available!");
        
        if (categories.isEmpty())
            throw new APIException("No categories have been create yet!");
        
        List<CategoryDTO> categoryDTOS
            = categories.stream()
                  .map(category -> modelMapper.map(category, CategoryDTO.class))
                  .toList();
        
        CategoryResponse categoryResponse = new CategoryResponse();
        categoryResponse.setContent(categoryDTOS);
        categoryResponse.setPageNumber(pageNumber);
        categoryResponse.setPageSize(pageSize);
        categoryResponse.setTotalElements(categoryPage.getTotalElements());
        categoryResponse.setTotalPages(categoryPage.getTotalPages());
        categoryResponse.setLastPage(categoryPage.isLast());
        
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
        categoryToUpdate.setCategoryName(category.getCategoryName());
        categoryRepository.save(categoryToUpdate);
        
        return modelMapper.map(categoryToUpdate, CategoryDTO.class);
    }
}
