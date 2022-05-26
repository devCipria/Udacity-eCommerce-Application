package com.example.demo.controllers;

import com.example.demo.TestUtils;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.ItemRepository;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private ItemRepository itemRepository = mock(ItemRepository.class);

    @Before
    public void setup() {
        // need an instance of ItemController;
        itemController = new ItemController();
        TestUtils.injectObject(itemController, "itemRepository", itemRepository);
    }

    @Test
    public void verify_get_all_items() {
        List<Item> items = new ArrayList<>();
        Item roundWidget = new Item(1L, "Round Widget", new BigDecimal(2.99), "A widget that is round");
        Item squareWidget = new Item(2L, "Square Widget", new BigDecimal(1.99), "A widget that is square");
        Item triangleWidget = new Item(3L, "Triangle Widget", new BigDecimal(4.99), "A widget that is triangular");
        items.addAll(Arrays.asList(roundWidget, squareWidget, triangleWidget));


        when(itemRepository.findAll()).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItems();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(items.get(0), responseItems.get(0));
        assertEquals(items.get(1), responseItems.get(1));
        assertEquals(items.get(2), responseItems.get(2));
    }

    @Test
    public void verify_get_item_by_id() {
        Item roundWidget = new Item(1L, "Round Widget", new BigDecimal(2.99), "A widget that is round");

        when(itemRepository.findById(1L)).thenReturn(Optional.of(roundWidget));
        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item responseItem = response.getBody();
        assertNotNull(responseItem);
        assertEquals(roundWidget, responseItem);
    }

    @Test
    public void verify_get_items_by_name() {
        List<Item> items = new ArrayList<>();
        Item triangleWidget = new Item(1L, "Triangle Widget", new BigDecimal(4.99), "A widget that is triangular");
        items.add(triangleWidget);

        when(itemRepository.findByName("Triangle Widget")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("Triangle Widget");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> responseItems = response.getBody();
        assertNotNull(responseItems);
        assertEquals(triangleWidget, responseItems.get(0));
    }

    @Test
    public void get_items_by_name_Failure() {
        List<Item> items = new ArrayList<>();
        Item triangleWidget = new Item(1L, "Triangle Widget", new BigDecimal(4.99), "A widget that is triangular");
        items.add(triangleWidget);

        when(itemRepository.findByName("Triangle Widget")).thenReturn(items);
        ResponseEntity<List<Item>> response = itemController.getItemsByName("no_such_item");

        assertEquals(404, response.getStatusCodeValue());
    }




}
