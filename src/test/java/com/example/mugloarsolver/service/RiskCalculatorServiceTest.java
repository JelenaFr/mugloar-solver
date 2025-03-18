package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class RiskCalculatorServiceTest {
    @InjectMocks
    private RiskCalculatorService riskCalculatorService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void calculateRisk_ShouldIncreaseRisk_WhenLivesAreLow() {
        // Given
        GameResponse game = new GameResponse();
        game.setLives(1);

        MessageResponse.Message message = new MessageResponse.Message();
        message.setReward("100");
        message.setProbability("Risky");

        // When
        int risk = riskCalculatorService.calculateRisk(message, game, List.of());

        // Then
        assertTrue(risk > 50);
    }

    @Test
    void calculateRisk_ShouldDecreaseRisk_WhenProtectionIsBought() {
        // Given
        GameResponse game = new GameResponse();
        game.setLives(5);

        MessageResponse.Message message = new MessageResponse.Message();
        message.setReward("100");
        message.setProbability("Playing with fire");

        List<ShopItem> purchasedItems = List.of(new ShopItem("1", "Iron Plating", 100));

        // When
        int risk = riskCalculatorService.calculateRisk(message, game, purchasedItems);

        // Then
        assertTrue(risk <= 50, "Risk should be reduced when protection is bought, but was: " + risk);
    }

}
