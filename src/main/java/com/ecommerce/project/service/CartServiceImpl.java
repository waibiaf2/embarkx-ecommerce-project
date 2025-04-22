package com.ecommerce.project.service;

import com.ecommerce.project.exceptions.APIException;
import com.ecommerce.project.exceptions.ResourceNotFoundException;
import com.ecommerce.project.model.Cart;
import com.ecommerce.project.model.CartItem;
import com.ecommerce.project.model.Product;
import com.ecommerce.project.payload.CartDTO;
import com.ecommerce.project.payload.ProductDTO;
import com.ecommerce.project.repositories.CartItemRepository;
import com.ecommerce.project.repositories.CartRepository;
import com.ecommerce.project.repositories.ProductRepository;
import com.ecommerce.project.utils.AuthUtil;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Stream;

@Service
public class CartServiceImpl implements CartService {
    private final ProductRepository productRepository;
    private final CartRepository cartRepository;
    private final AuthUtil authUtil;
    private final CartItemRepository cartItemRepository;
    private final ModelMapper modelMapper;
    
    public CartServiceImpl(
        ProductRepository productRepository,
        CartRepository cartRepository,
        AuthUtil authUtils,
        CartItemRepository cartItemRepository,
        ModelMapper modelMapper
    ) {
        this.productRepository = productRepository;
        this.cartRepository = cartRepository;
        this.authUtil = authUtils;
        this.cartItemRepository = cartItemRepository;
        this.modelMapper = modelMapper;
    }
    
    
    @Override
    public CartDTO addProductToCart(Long productId, Integer quantity) {

        Cart cart = createCart();
        
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new ResourceNotFoundException("Product", "id", productId)
        );
        
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cart.getCartId(), productId);
        
        // Perform validations
        if(cartItem != null)
            throw new APIException("Product" + product.getProductName() + " already exists in the cart");
        
        if(product.getQuantity() == 0)
            throw new APIException("Product " + product.getProductName() + " is not available");
        
        if(product.getQuantity() < quantity)
            throw new APIException(
                "Please make an order of " + product.getProductName() + "less than or equal to" + product.getQuantity());
        
        // Create Cart Item
        CartItem cartItemToAdd = new CartItem();
        
        cartItemToAdd.setProduct(product);
        cartItemToAdd.setQuantity(quantity);
        cartItemToAdd.setDiscount(product.getDiscount());
        cartItemToAdd.setProductPrice(product.getSpecialPrice());
        cartItemToAdd.setCart(cart);

        // Save Cart Item
        cartItemRepository.save(cartItemToAdd);
        
        product.setQuantity(product.getQuantity() - quantity);
        cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
        cartRepository.save(cart);
        
        List<CartItem> cartItems = cart.getCartItems();
        
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        return getUpdateCartDTO(cartItems, cartDTO);
    }
    
    @Override
    public List<CartDTO> getAllCarts() {
        List<Cart> carts = cartRepository.findAll();
        
        if (carts.isEmpty())
            throw new APIException("No cart exists");
        
        return carts.stream().map(cart ->{
            CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
            
            List<ProductDTO> products = cart.getCartItems().stream().map(
                cartItem -> {
                    cartItem.getProduct().setQuantity(cartItem.getQuantity());
                    return modelMapper.map(cartItem.getProduct(), ProductDTO.class);
                }
            ).toList();
            
            cartDTO.setProducts(products);
            
            return cartDTO;
        }).toList();
    }
    
    @Override
    public CartDTO getUserCart(String emailId, Long cartId) {
        Cart cart = cartRepository.findCartByEmailAndCartId(emailId, cartId);
        if (cart == null)
            throw new ResourceNotFoundException("Cart", "cartId", cartId);
        
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        cart.getCartItems().forEach(
            cartItem -> cartItem.getProduct().setQuantity(cartItem.getQuantity())
        );
        List<ProductDTO> products = cart.getCartItems().stream().map(
            cartItem -> modelMapper.map(cartItem.getProduct(), ProductDTO.class)
        ).toList();
        cartDTO.setProducts(products);
        
        return cartDTO;
    }
    
    @Transactional
    @Override
    public CartDTO updateProductQuantityInCart(Long productId, Integer quantity) {
        String emailId = authUtil.loggedInEmail();
        Cart userCart = cartRepository.findCartByEmail(emailId);
        Long cartId = userCart.getCartId();
        
        Cart cart = cartRepository.findById(cartId).orElseThrow(
            () -> new ResourceNotFoundException("Cart", "cartId", cartId)
        );
        
        Product product = productRepository.findById(productId).orElseThrow(
            () -> new ResourceNotFoundException("Product", "id", productId)
        );
        
        if(product.getQuantity() == 0)
            throw new APIException("Product " + product.getProductName() + " is not available");
        
        if(product.getQuantity() < quantity)
            throw new APIException(
                "Please make an order of " + product.getProductName() + "less than or equal to" + product.getQuantity());
        
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        if( cartItem == null )
            throw new APIException("Product " + product.getProductName() + " is not available in the cart");
        
        int newQuantity = cartItem.getQuantity() + quantity;
        
        if(newQuantity < 0)
            throw new APIException("Product " + product.getProductName() + " quantity cannot be negative");
        
        if (newQuantity == 0) {
            deleteProductFromCart(cartId, productId);
        } else {
            cartItem.setProductPrice(product.getSpecialPrice());
            cartItem.setQuantity(newQuantity);
            cartItem.setDiscount(product.getDiscount());
            cart.setTotalPrice(cart.getTotalPrice() + (product.getSpecialPrice() * quantity));
            cartRepository.save(cart);
        }
        
        CartItem updatedCartItem = cartItemRepository.save(cartItem);
        if (updatedCartItem.getQuantity() == 0)
            cartItemRepository.delete(updatedCartItem);
        
        CartDTO cartDTO = modelMapper.map(cart, CartDTO.class);
        
        List<CartItem> cartItems = cart.getCartItems();
        
        return getUpdateCartDTO(cartItems, cartDTO);
    }
    
    
    @Transactional
    @Override
    public String deleteProductFromCart(Long cartId, Long productId) {
        Cart cart = cartRepository.findById(cartId).orElseThrow(
            () -> new ResourceNotFoundException("Cart", "cartId", cartId)
        );
        
        CartItem cartItem = cartItemRepository.findCartItemByProductIdAndCartId(cartId, productId);
        
        if (cartItem == null)
            throw new APIException("Product " + productId + " is not available in the cart");
        
        cart.setTotalPrice(cart.getTotalPrice() - (cartItem.getProductPrice() * cartItem.getQuantity()));
        
        cartItemRepository.deleteCartItemByProductIdAndCartId(cartId, productId);
        
        return "Product " + cartItem.getProduct().getProductName() + " removed from the cart !!!";
    }
    
    
    private Cart createCart() {
        Cart userCart  = cartRepository.findCartByEmail(authUtil.loggedInEmail());
        if(userCart != null){
            return userCart;
        }
        
        Cart cart = new Cart();
        cart.setTotalPrice(0.00);
        cart.setUser(authUtil.loggedInUser());
        cartRepository.save(cart);
        return cart;
    }
    
   
    private CartDTO getUpdateCartDTO(List<CartItem> cartItems, CartDTO cartDTO) {
        Stream<ProductDTO> productStream  = cartItems.stream().map(
            item -> {
                ProductDTO productMap = modelMapper.map(item.getProduct(), ProductDTO.class);
                productMap.setQuantity(item.getQuantity());
                return productMap;
            }
        );
        
        cartDTO.setProducts(productStream.toList());
        
        return cartDTO;
    }
}
