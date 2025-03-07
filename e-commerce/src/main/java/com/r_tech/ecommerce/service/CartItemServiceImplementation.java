package com.r_tech.ecommerce.service;

import com.r_tech.ecommerce.exception.CartItemException;
import com.r_tech.ecommerce.exception.UserException;
import com.r_tech.ecommerce.model.Cart;
import com.r_tech.ecommerce.model.CartItem;
import com.r_tech.ecommerce.model.Product;
import com.r_tech.ecommerce.model.User;
import com.r_tech.ecommerce.repository.CartItemRepository;
import com.r_tech.ecommerce.repository.CartRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CartItemServiceImplementation implements CartItemService {

    private CartItemRepository cartItemRepository;
    private UserService userService;
    private CartRepository cartRepository;

    public CartItemServiceImplementation(CartItemRepository cartItemRepository, CartRepository cartRepository, UserService userService) {
        this.cartItemRepository = cartItemRepository;
        this.cartRepository = cartRepository;
        this.userService = userService;
    }


    @Override
    public CartItem createCartItem(CartItem cartItem) {
        cartItem.setQuantity(1);
        cartItem.setPrice(cartItem.getProduct().getPrice() * cartItem.getQuantity());
        cartItem.setDiscountedPrice(cartItem.getProduct().getDiscountedPrice() * cartItem.getQuantity());

        CartItem createdCartItem = cartItemRepository.save(cartItem);

        return createdCartItem;
    }

    @Override
    public CartItem updateCartItem(Long userId, Long cartItemId, int quantity) throws CartItemException, UserException {

        CartItem item = findCartItemById(cartItemId);
        if (item == null) {
            throw new CartItemException("Cart Item Not Found");
        }

        User user = userService.findUserById(item.getUserId());

        if (user.getId().equals(userId)) {
            item.setQuantity(quantity);

            item.setPrice(item.getQuantity() * item.getProduct().getPrice());
            item.setDiscountedPrice(item.getQuantity() * item.getProduct().getDiscountedPrice());
        } else {
            throw new UserException("User does not have permission to update this cart item");
        }

        return cartItemRepository.save(item);
    }

    @Override
    public CartItem isCardItemExist(Cart cart, Product product, Long userId) {

        CartItem cartItem = cartItemRepository.isCartItemExist(cart, product, userId);

        return cartItem;

    }

    @Override
    public void removeCartItem(Long UserId, Long cartItemId) throws CartItemException, UserException {

        CartItem cartItem = findCartItemById(cartItemId);

        User user = userService.findUserById(cartItem.getUserId());

        User reqUser = userService.findUserById(UserId);

        if (user.getId().equals(reqUser.getId())) {
            cartItemRepository.deleteById(cartItemId);
        } else {
            throw new UserException("You can't remove another users item");
        }

    }

    @Override
    public CartItem findCartItemById(Long cartItemId) throws CartItemException {

        Optional<CartItem> opt = cartItemRepository.findById(cartItemId);

        if (opt.isPresent()) {
            return opt.get();
        } else {
            throw new CartItemException("CartItem not found with id: " + cartItemId);
        }


    }

}
