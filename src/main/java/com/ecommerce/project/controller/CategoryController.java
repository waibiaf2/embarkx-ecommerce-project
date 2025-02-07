package com.ecommerce.project.controller;

import com.ecommerce.project.model.Category;
import com.ecommerce.project.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {
    /**
     * Constructor Injection
     *****************************************/
    /*private final CategoryService categoryService;

    public CategoryController(CategoryService categoryService) {
        this.categoryService = categoryService;
    }*/

    /****************************************************
     * Field Injection
     * *********************/
    @Autowired
    private CategoryService categoryService;

    @GetMapping("/public/categories")
    public List<Category> getCategories() {
        return categoryService.getCategories();
    }

    @PostMapping("/admin/categories")
    public String createCategory(@RequestBody Category category) {
        categoryService.createCategory(category);
        return "Category created successfully";
    }
}
