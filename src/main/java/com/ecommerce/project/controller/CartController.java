package com.ecommerce.project.controller;

import com.ecommerce.project.config.AppConstants;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.service.CartServiceImpl;
import com.ecommerce.project.utils.AuthUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(AppConstants.BASE_URL)
public class CartController {
    @Autowired
    private CartServiceImpl cartService;
    @Autowired
    private AuthUtil authUtil;
    @Autowired
    private CartRepository cartRepository;
    
    @PostMapping("/carts/products/{productId}/quantity/{quantity}")
    public ResponseEntity<CartDTO> addProductToCart(
        @PathVariable(name = "productId") Long productId,
        @PathVariable(name = "quantity") Integer quantity
    ) {
        CartDTO cartDTO = cartService.addProductToCart(productId, quantity);
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
    
    @GetMapping("/carts")
    public ResponseEntity<List<CartDTO>> getAllCarts() {
        List<CartDTO> cartDTOs = cartService.getAllCarts();
        return new ResponseEntity<>(cartDTOs, HttpStatus.OK);
    }
    
    @GetMapping("/carts/users/cart")
    public ResponseEntity<CartDTO> getUserCart() {
        String emailId = authUtil.loggedInEmail();
        Long cartId = cartRepository.findCartByEmail(emailId).getCartId();
        
        CartDTO cartDTO = cartService.getUserCart(emailId, cartId);
        
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
    
    @PutMapping("/cart/products/{productId}/quantity/{operation}")
    public ResponseEntity<CartDTO> updateCartProductQuantity(
        @PathVariable(name = "productId") Long productId,
        @PathVariable(name = "operation") String operation
    ) {
        // increase, decrease
        CartDTO cartDTO = cartService.updateProductQuantityInCart(
            productId,
            operation.equalsIgnoreCase("delete") ? -1 : 1
        );
        
        return new ResponseEntity<>(cartDTO, HttpStatus.OK);
    }
    
    @DeleteMapping("/cart/{cartId}/products/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
        @PathVariable(name = "productId") Long productId,
        @PathVariable(name = "cartId") Long cartId
    ) {
        String status = cartService.deleteProductFromCart(cartId, productId);
        return new ResponseEntity<>(status, HttpStatus.OK);
    }
}
