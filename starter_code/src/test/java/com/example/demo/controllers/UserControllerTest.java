package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private UserController userController;
    private UserRepository userRepository = mock(UserRepository.class);
    private CartRepository cartRepository = mock(CartRepository.class);
    private BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);

    @Before
    public void setup() {
        userController = new UserController();
        TestUtils.injectObject(userController, "userRepository", userRepository);
        TestUtils.injectObject(userController, "cartRepository", cartRepository);
        TestUtils.injectObject(userController, "bCryptPasswordEncoder", encoder);

    }

    @Test
    public void create_user_happy_path() {
        when(encoder.encode("password")).thenReturn("HashedPassword");

        CreateUserRequest userRequest = new CreateUserRequest();
        userRequest.setUsername("user");
        userRequest.setPassword("password");
        userRequest.setConfirmPassword("password");

        final ResponseEntity<User> response = userController.createUser(userRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("HashedPassword", user.getPassword());
        assertEquals("user", user.getUsername());
    }

    @Test
    public void verify_find_user_by_id() {
        Long id = 5L;
        User user = new User(id, "David", "abc1234");

        when(userRepository.findById(id)).thenReturn(Optional.of(user));
        final ResponseEntity<User> response = userController.findById(id);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(user.getUsername(), responseUser.getUsername());
        assertEquals(user.getPassword(), responseUser.getPassword());
        assertEquals(user.getId(), responseUser.getId());
    }

    @Test
    public void verify_find_user_by_username() {
        String username = "David";
        User user = new User(10, username, "abc1234");

        when(userRepository.findByUsername(username)).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName(username);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User responseUser = response.getBody();
        assertNotNull(responseUser);
        assertEquals(user.getUsername(), responseUser.getUsername());
        assertEquals(user.getPassword(), responseUser.getPassword());
        assertEquals(user.getId(), responseUser.getId());
    }

    @Test
    public void find_by_username_Failure() {
        User user = new User(10, "David", "abc1234");

        when(userRepository.findByUsername("David")).thenReturn(user);
        ResponseEntity<User> response = userController.findByUserName("no_such_username");

        assertEquals(404, response.getStatusCodeValue());
    }

}
