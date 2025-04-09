package com.ecommerce.project.service;

import com.ecommerce.project.model.Cart;
import com.ecommerce.project.payload.CartDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface CartService {
    CartDTO addProductToCart(Long productId, Integer quantity);
    List<CartDTO> getAllCarts();
    CartDTO getUserCart(String emailId, Long cartId);
    @Transactional
    CartDTO updateProductQuantityInCart(Long productId, Integer quantity);
    
    @Transactional
    String deleteProductFromCart(Long cartId, Long productId);
}
