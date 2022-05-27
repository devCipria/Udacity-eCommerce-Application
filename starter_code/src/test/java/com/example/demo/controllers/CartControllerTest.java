package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private CartController cartController;
    private CartRepository cartRepository = mock(CartRepository.class);
    private UserRepository userRepository = mock(UserRepository.class);
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, "cartRepository", cartRepository);
        TestUtils.injectObject(cartController, "userRepository", userRepository);
        TestUtils.injectObject(cartController, "itemRepository", itemRepository);
    }

    @Test
    public void verify_add_to_cart() {
        User user = new User(1L, "David", "abc1234");

        Item roundWidget = new Item(1L, "Round Widget", new BigDecimal(2.99), "A widget that is round");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(roundWidget);

        user.setCart(cart);

        ModifyCartRequest cartRequest = new ModifyCartRequest("David", 1L, 1);

        when(userRepository.findByUsername("David")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(roundWidget));
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertEquals(cart.getId(), responseCart.getId());
        assertEquals("David", responseCart.getUser().getUsername());
        assertEquals(roundWidget, responseCart.getItems().get(0));
        assertEquals(cart.getTotal(), responseCart.getTotal());
    }

    @Test
    public void verify_remove_from_cart() {
        User user = new User(2L, "Mary", "abc1234");

        Item roundWidget = new Item(1L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        Item squareWidget = new Item(2L, "Square Widget", new BigDecimal(1.99), "A widget that is square");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(roundWidget);
        cart.addItem(squareWidget);

        user.setCart(cart);

        ModifyCartRequest cartRequest = new ModifyCartRequest("Mary", 1L, 1);

        // Remove first item: roundWidget
        when(userRepository.findByUsername("Mary")).thenReturn(user);
        when(itemRepository.findById(1L)).thenReturn(Optional.of(roundWidget));
        ResponseEntity<Cart> response = cartController.removeFromcart(cartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart responseCart = response.getBody();
        assertNotNull(responseCart);
        assertEquals(cart.getId(), responseCart.getId());
        assertEquals(1, responseCart.getItems().size());
        assertEquals(squareWidget, responseCart.getItems().get(0));
        assertFalse(responseCart.getItems().contains(roundWidget));
    }

    @Test
    public void add_to_cart_failure() {

        // user Tom does not exist
        ModifyCartRequest cartRequest = new ModifyCartRequest("Tom", 1L, 1);
        when(userRepository.findByUsername("Tom")).thenReturn(null);
        when(itemRepository.findById(8L)).thenReturn(Optional.of(new Item()));
        ResponseEntity<Cart> response = cartController.addTocart(cartRequest);

        assertEquals(response.getStatusCodeValue(), HttpStatus.NOT_FOUND.value());
    }

}
