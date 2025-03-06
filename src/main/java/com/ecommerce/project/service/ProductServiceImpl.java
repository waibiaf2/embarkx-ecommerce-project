package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Category;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.payload.ProductResponse;
import com.ecommerce.project.repositories.CategoryRepository;
import com.ecommerce.project.repositories.ProductRepository;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.List;

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
    public ProductDTO addProduct(ProductDTO productDTO, Long categoryId) {
        Category category = categoryRepository
            .findById(categoryId)
            .orElseThrow(() -> new ResourceNotFoundException("Category", "categoryId", categoryId));
        Product product = modelMapper.map(productDTO, Product.class);
        
        product.setImage("default.png");
        product.setCategory(category);
        double specialPrice = product.getPrice() -
            ((product.getDiscount() / 100) * product.getPrice());
        product.setSpecialPrice(specialPrice);
        Product savedProduct = productRepository.save(product);
        
        return modelMapper.map(savedProduct, ProductDTO.class);
    }
    
    @Override
    public ProductDTO getProductById(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
            
        return modelMapper.map(product, ProductDTO.class);
    }
    
    @Override
    public ProductResponse getAllProducts(Integer pageNumber, Integer pageSize, String sortBy, String orderBy) {
        Sort sortOrder = orderBy.equalsIgnoreCase("asc") ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();
        
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortOrder);
        Page<Product> productsPage = productRepository.findAll(pageDetails);
        List<Product> products = productsPage.getContent();
        if (products.isEmpty())
            throw new APIException("There are no products created yet!");
        
        List<ProductDTO> productDTOS = products.stream()
            .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPage(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        
        return productResponse;
    }
    
    @Override
    public ProductResponse getProductsByCategory(
        Long categoryId,
        Integer pageNumber,
        Integer pageSize,
        String sortBy,
        String orderBy
    ) {
        Category category = categoryRepository
            .findById(categoryId)
            .orElseThrow(
                () -> new ResourceNotFoundException("Category", "categoryId", categoryId)
            );
        
        Sort sortOrder = orderBy.equalsIgnoreCase("asc") ?
            Sort.by(sortBy).ascending() :
            Sort.by(sortBy).descending();
        Pageable pageDetails = PageRequest.of(pageNumber, pageSize, sortOrder);
        Page<Product> productsPage = productRepository.findByCategory(category, pageDetails);
        List<Product>  products = productsPage.getContent();
        
        List<ProductDTO> productDTOS = products.stream()
            .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        productResponse.setPage(pageNumber);
        productResponse.setPageSize(pageSize);
        productResponse.setTotalElements(productsPage.getTotalElements());
        productResponse.setTotalPages(productsPage.getTotalPages());
        productResponse.setLastPage(productsPage.isLast());
        
        return productResponse;
    }
    
    @Override
    public ProductResponse getProductsByKeyWord(String keyword) {
        List<Product> products = productRepository.findByProductNameLikeIgnoreCase('%' + keyword + '%');
        
        if(products.isEmpty())
            throw new APIException("There are no products matching keyword: " + keyword);
        
        List<ProductDTO> productDTOS = products.stream()
            .map(product -> modelMapper.map(product, ProductDTO.class)).toList();
        
        ProductResponse productResponse = new ProductResponse();
        productResponse.setContent(productDTOS);
        
        return productResponse;
    }
    
    @Override
    public ProductDTO updateProduct(Long productId, ProductDTO productDTO) {
        Product product = modelMapper.map(productDTO, Product.class);
        Product existingProduct = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
        
        existingProduct.setProductName(product.getProductName());
        if (product.getPrice() != null)
            existingProduct.setPrice(product.getPrice());
        existingProduct.setDiscount(product.getDiscount());
        existingProduct.setQuantity(product.getQuantity());
        existingProduct.setDescription(product.getDescription());
        
        if(product.getDiscount() != null) {
            double specialPrice = product.getPrice() -
                ((product.getDiscount() / 100) * product.getPrice());
            existingProduct.setSpecialPrice(specialPrice);
        }
        
        productRepository.save(existingProduct);
        
        return modelMapper.map(existingProduct, ProductDTO.class);
    }
    
    @Override
    public ProductDTO deleteProduct(Long productId) {
        Product product = productRepository.findById(productId)
            .orElseThrow(() -> new ResourceNotFoundException("Product", "Id", productId));
        productRepository.delete(product);
        
        return modelMapper.map(product, ProductDTO.class);
    }
}
