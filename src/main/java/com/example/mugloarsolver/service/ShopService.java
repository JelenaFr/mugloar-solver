package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.ShopResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ShopService {

    private static final Logger logger = LoggerFactory.getLogger(ShopService.class);
    private static final String BASE_URL = "https://dragonsofmugloar.com/api/v2";
    private final RestTemplate restTemplate;

    public ShopService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ShopItem> checkAndBuyItems(GameResponse game) {
        if (game.getGold() == 0) {
            logger.warn("🛍️ No gold available, skipping shop check.");
            return List.of();
        }
        List<ShopItem> shopItems = getShopItems(game.getGameId());
        if (shopItems == null || shopItems.isEmpty()) {
            logger.warn("⚠️ No items available in the shop.");
            return List.of();
        }
        logger.info("📦 {} items available in shop: {}", shopItems.size(),
                shopItems.stream()
                        .map(item -> String.format("Name: %s, Cost: %d", item.getName(), item.getCost()))
                        .toList());
        List<ShopItem> purchasedItems = new ArrayList<>();
        for (ShopItem item : shopItems) {
            if (game.getGold() >= item.getCost()) {
                ShopResponse response = buyItem(game.getGameId(), item.getId());

                if (response != null) {
                    game.setGold(response.getGold());
                    game.setLives(response.getLives());
                    purchasedItems.add(item);
                    logger.info("✅ Purchase successful! Bought '{}'. Remaining Gold: {}, Lives: {}",
                            item.getName(), game.getGold(), game.getLives());
                }
            }
        }
        return purchasedItems;
    }

    List<ShopItem> getShopItems(String gameId) {
        String url = BASE_URL + "/" + gameId + "/shop";
        logger.info("🛍️ Fetching shop items: GET {}", url);
        try {
            ResponseEntity<List<ShopItem>> response = restTemplate.exchange(
                    url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
                    });
            return response.getBody();
        } catch (HttpClientErrorException e) {
            logger.error("❌ HTTP error while getting shop items: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }

    ShopResponse buyItem(String gameId, String itemId) {
        String url = BASE_URL + "/" + gameId + "/shop/buy/" + itemId;
        logger.info("💰 Buying item: POST {}", url);
        try {
            return restTemplate.postForObject(url, null, ShopResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("❌ HTTP error while buying item: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
}