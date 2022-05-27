package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private OrderController orderController;
    private UserRepository userRepository = mock(UserRepository.class);
    private OrderRepository orderRepository = mock(OrderRepository.class);

    @Before
    public void setup() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, "userRepository", userRepository);
        TestUtils.injectObject(orderController, "orderRepository", orderRepository);
    }

    @Test
    public void verify_submit() {
        // Create a user with a cart of items
        User user = new User(5L, "David", "abc1234");
        Item roundWidget = new Item(1L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        Item squareWidget = new Item(2L, "Square Widget", new BigDecimal(1.99), "A widget that is square");

        Cart cart = new Cart();
        cart.setId(1L);
        cart.setUser(user);
        cart.addItem(roundWidget);
        cart.addItem(squareWidget);

        user.setCart(cart);

        List<UserOrder> userOrderList = new ArrayList<>();
        UserOrder userOrder = UserOrder.createFromCart(cart);
        userOrderList.add(userOrder);

//        when(orderRepository.findByUser(user)).thenReturn((List<UserOrder>) userOrderList);
        when(userRepository.findByUsername("David")).thenReturn(user);
        ResponseEntity<UserOrder> response = orderController.submit("David");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder responseOrder = response.getBody();
        assertNotNull(responseOrder);
        assertEquals(userOrder.getId(), responseOrder.getId());
        assertEquals(5, responseOrder.getUser().getId());
        assertEquals(Arrays.asList(roundWidget, squareWidget), responseOrder.getItems());

    }
}
