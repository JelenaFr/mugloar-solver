package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.ShopResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.mockito.ArgumentMatchers.isNull;

public class ShopServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getShopItems_ShouldReturnListOfItems() {
        // Given
        ShopItem item = new ShopItem();
        item.setId("hpot");
        item.setName("Healing Potion");
        item.setCost(50);

        List<ShopItem> mockItems = List.of(item);
        ResponseEntity<List<ShopItem>> mockResponse = ResponseEntity.ok(mockItems);

        when(restTemplate.exchange(anyString(), any(), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        // When
        List<ShopItem> items = shopService.getShopItems("testGameId");

        // Then
        assertNotNull(items);
        assertEquals(1, items.size());
        assertEquals("Healing Potion", items.get(0).getName());
    }

    @Test
    void buyItem_ShouldReturnUpdatedShopResponse() {
        // Given
        ShopResponse mockResponse = new ShopResponse();
        mockResponse.setGold(100);
        mockResponse.setLives(3);

        when(restTemplate.postForObject(anyString(), eq(null), eq(ShopResponse.class)))
                .thenReturn(mockResponse);

        // When
        ShopResponse response = shopService.buyItem("testGameId", "hpot");

        // Then
        assertNotNull(response);
        assertEquals(100, response.getGold());
        assertEquals(3, response.getLives());
    }

    @Test
    void getShopItems_ShouldThrowException_WhenRequestFails() {
        // Given
        when(restTemplate.exchange(anyString(), any(), any(), eq(new ParameterizedTypeReference<List<ShopItem>>() {})))
                .thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));

        // When & Then
        assertThrows(HttpClientErrorException.class, () -> shopService.getShopItems("12345"));
    }
}