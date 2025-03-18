package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.ReputationResponse;
import com.example.mugloarsolver.model.response.ShopResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

class GameServiceTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private GameService gameService;

    @InjectMocks
    private ReputationService reputationService;

    @InjectMocks
    private ShopService shopService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void startGame_ShouldReturnGameResponse() {
        // Given
        GameResponse mockResponse = new GameResponse();
        mockResponse.setGameId("12345");
        when(restTemplate.postForObject(anyString(), eq(null), eq(GameResponse.class)))
                .thenReturn(mockResponse);

        // When
        GameResponse response = gameService.startGame();

        // Then
        assertNotNull(response);
        assertEquals("12345", response.getGameId());
        verify(restTemplate, times(1)).postForObject(anyString(), eq(null), eq(GameResponse.class));
    }

    @Test
    void getReputation_ShouldReturnReputationResponse() {
        // Given
        ReputationResponse mockResponse = new ReputationResponse();
        mockResponse.setPeople(10);
        mockResponse.setState(5);
        mockResponse.setUnderworld(2);

        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenReturn(mockResponse);

        // When
        ReputationResponse response = reputationService.getReputation("12345");

        // Then
        assertNotNull(response);
        assertEquals(10, response.getPeople());
        assertEquals(5, response.getState());
        assertEquals(2, response.getUnderworld());
    }

    @Test
    void getReputation_ShouldThrowException_WhenGameOver() {
        // Given
        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.GONE, "Game Over"));

        // When & Then
        assertThrows(IllegalStateException.class, () -> reputationService.getReputation("12345"));
    }

    @Test
    void checkAndBuyItems_ShouldBuyItem_WhenEnoughGold() {
        // Given
        GameResponse game = new GameResponse();
        game.setGameId("12345");
        game.setGold(200);
        game.setLives(3);

        List<ShopItem> shopItems = List.of(new ShopItem("hpot", "Healing Potion", 50));

        when(restTemplate.exchange(anyString(), eq(HttpMethod.GET), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(new ResponseEntity<>(shopItems, HttpStatus.OK));

        ShopResponse mockShopResponse = new ShopResponse();
        mockShopResponse.setGold(150);
        mockShopResponse.setLives(4);

        when(restTemplate.postForObject(anyString(), eq(null), eq(ShopResponse.class)))
                .thenReturn(mockShopResponse);

        // When
        List<ShopItem> purchasedItems = shopService.checkAndBuyItems(game);

        // Then
        assertEquals(1, purchasedItems.size());
        assertEquals(150, game.getGold());
        assertEquals(4, game.getLives());
    }

}
