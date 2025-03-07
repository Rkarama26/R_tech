package com.r_tech.ecommerce.service;

import com.r_tech.ecommerce.repository.CartRepository;
import com.r_tech.ecommerce.exception.CartNotFoundException;
import com.r_tech.ecommerce.exception.ProductException;
import com.r_tech.ecommerce.model.Cart;
import com.r_tech.ecommerce.model.CartItem;
import com.r_tech.ecommerce.model.Product;
import com.r_tech.ecommerce.model.User;
import com.r_tech.ecommerce.request.AddItemRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class CartServiceImplemetation implements CartService {


    private CartRepository cartRepository;
    private CartItemService cartItemService;
    private ProductService productService;

    public CartServiceImplemetation(CartItemService cartItemService, CartRepository cartRepository, ProductService productService) {
        this.cartItemService = cartItemService;
        this.cartRepository = cartRepository;
        this.productService = productService;
    }

    @Override
    public Cart createCart(User user) {

        Cart cart = new Cart();
        cart.setUser(user);

        return cartRepository.save(cart);

    }

    @Override
    public String addItemToCart(Long userId, AddItemRequest req) throws ProductException {

        Cart cart = cartRepository.findByUserId(userId);
        if (cart == null) {
            throw new CartNotFoundException("Cart not found for user with ID " + userId);
        }

        Product product = productService.findProductById(req.getProductId());
        if (product == null) {
            throw new ProductException("Product not found with ID: " + req.getProductId());
        }


        CartItem isPresent = cartItemService.isCardItemExist(cart, product, userId);

        if (isPresent == null) {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setCart(cart);
            cartItem.setQuantity(req.getQuantity());
            cartItem.setUserId(userId);


            int price = req.getQuantity() * product.getPrice();
            cartItem.setPrice(price);

            CartItem createCartItem = cartItemService.createCartItem(cartItem);

            cart.getCartItems().add(createCartItem);
        } else {
            isPresent.setQuantity(isPresent.getQuantity() + req.getQuantity());
            int updatedPrice = isPresent.getQuantity() * product.getPrice();
            isPresent.setPrice(updatedPrice);

            isPresent.setPrice(updatedPrice);
        }

        return "Item Added To Cart";
    }

    @Override
    public Cart findUserCart(Long userId) {

        Cart cart = cartRepository.findByUserId(userId);

        int totalPrice = 0;
        int totalDiscountedPrice = 0;
        int totalItem = 0;

        for (CartItem cartItem : cart.getCartItems()) {
            totalPrice = totalPrice + cartItem.getPrice();
            totalDiscountedPrice = totalDiscountedPrice + cartItem.getDiscountedPrice();
            totalItem = totalItem + cartItem.getQuantity();
        }

        cart.setTotalDiscountedPrice(totalDiscountedPrice);
        cart.setTotalItem(totalItem);
        cart.setTotalPrice(totalPrice);
        cart.setDiscount(totalPrice - totalDiscountedPrice);

        return cartRepository.save(cart);
    }

}
