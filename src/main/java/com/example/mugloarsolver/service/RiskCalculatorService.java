package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.MessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class RiskCalculatorService {

    private static final Logger logger = LoggerFactory.getLogger(RiskCalculatorService.class);

    public int calculateRisk(MessageResponse.Message message, GameResponse game, List<ShopItem> purchasedItems) {
        int risk = 0;
        int expiresIn = Optional.ofNullable(message.getExpiresIn()).orElse(0);
        int reward = safeParseInt(message.getReward());
        String probability = Optional.ofNullable(message.getProbability()).orElse("").trim();

        boolean hasProtection = hasProtection(purchasedItems);


        if (game.getLives() < 3)
            risk += 50;
        else if (game.getLives() > 10)
            risk -= 20;

        if (expiresIn < 2)
            risk += 50;
        else if (expiresIn < 5)
            risk += 20;


        if (reward > 100)
            risk += 30;

        switch (probability) {
            case "Sure thing" -> risk -= 40;
            case "Piece of cake" -> risk -= 30;
            case "Walk in the park" -> risk -= 20;
            case "Quite likely" -> risk -= 10;
            case "Risky" -> risk += 20;
            case "Rather detrimental" -> risk += 50;
            case "Playing with fire" -> risk += hasProtection ? 0 : 100;
            case "Suicide mission" -> risk += hasProtection ? 70 : 150;
            default -> logger.warn("⚠️ Unknown probability '{}', risk unchanged", probability);
        }

        return risk;
    }

    private boolean hasProtection(List<ShopItem> purchasedItems) {
        return purchasedItems != null && purchasedItems.stream()
                .anyMatch(item -> item.getName().toLowerCase().contains("plating") || item.getName().toLowerCase().contains("potion"));
    }

    public int safeParseInt(String str) {
        try {return Integer.parseInt(str);} catch (NumberFormatException e) {return 0;}
    }
}
