package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.service.ProductService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(AppConstants.BASE_URL)
public class ProductController {
    private final ProductService productService;
    
    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    
    @PostMapping("/admin/category/{categoryId}/product")
    public ResponseEntity<ProductDTO> addProduct(@RequestBody Product product, @PathVariable Long categoryId) {
        ProductDTO savedProductDTO = productService.addProduct(product, categoryId);
        return new ResponseEntity<>(savedProductDTO, HttpStatus.CREATED);
    }
    
    @GetMapping("/public/products")
    public ResponseEntity<ProductResponse> getAllProducts(
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
        @RequestParam(name="sortBy", defaultValue = "categoryId", required = false) String sortBy,
        @RequestParam(name="orderBy", defaultValue = "asc", required = false) String orderBy
    ) {
        ProductResponse productResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, orderBy);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    
    @GetMapping("/public/products/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }
    
    @GetMapping("/public/category/{categoryId}/products")
    public ResponseEntity<ProductResponse> getProductsByCategory(
        @PathVariable Long categoryId,
        @RequestParam(name="pageNumber", defaultValue = AppConstants.PAGE_NUMBER,required = false) Integer pageNumber,
        @RequestParam(name="pageSize", defaultValue = AppConstants.PAGE_SIZE,required = false) Integer pageSize,
        @RequestParam(name="sortBy", defaultValue = "categoryId", required = false) String sortBy,
        @RequestParam(name="orderBy", defaultValue = "asc", required = false) String orderBy
    ) {
        ProductResponse productResponse = productService.getProductsByCategory(categoryId);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    
    @GetMapping("/public/products/keyword/{keyword}")
    public ResponseEntity<ProductResponse> findProductsByKeyWord(@PathVariable String keyword) {
        ProductResponse productResponse = productService.getProductsByKeyWord(keyword);
        return new ResponseEntity<>(productResponse, HttpStatus.OK);
    }
    
    @PutMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@RequestBody Product product, @PathVariable Long productId) {
        ProductDTO updatedProductDTO = productService.updateProduct(productId, product);
        return new ResponseEntity<>(updatedProductDTO, HttpStatus.OK);
    }
    
    @DeleteMapping("/admin/products/{productId}")
    public ResponseEntity<ProductDTO> deleteProduct(@PathVariable Long productId) {
        ProductDTO deletedProductDTO = productService.deleteProduct(productId);
        return new ResponseEntity<>(deletedProductDTO, HttpStatus.OK);
    }
}
