package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.MessageResponse;
import com.example.mugloarsolver.model.response.SolveMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

@Service
public class GameService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final String BASE_URL = "https://dragonsofmugloar.com/api/v2";
    private final RestTemplate restTemplate;
    private final ShopService shopService;
    private final MessageService messageService;
    private final ReputationService reputationService;

    public GameService(RestTemplate restTemplate, ShopService shopService, MessageService messageService, ReputationService reputationService) {
        this.restTemplate = restTemplate;
        this.shopService = shopService;
        this.messageService = messageService;
        this.reputationService = reputationService;
    }

    public GameResponse autoPlay() {
        GameResponse game = startGame();
        logger.info("🎮 New game started! gameId={}, Lives={}, Gold={}, Score={}", game.getGameId(), game.getLives(), game.getGold(), game.getScore());

        while (game.getLives() > 0 && game.getScore() < 1000) {
            try {
                try {
                    reputationService.getReputation(game.getGameId());
                } catch (IllegalStateException e) {
                    logger.error("❌ Game Over detected during reputation check");
                    break;
                }
                List<ShopItem> purchasedItems = shopService.checkAndBuyItems(game);
                List<MessageResponse.Message> messages = messageService.getMessages(game.getGameId());
                if (messages == null || messages.isEmpty()) {
                    logger.warn("⚠️ No quests available, skipping turn.");
                    break;
                }
                Optional<MessageResponse.Message> bestMessage = messageService.chooseBestMessage(messages, game, purchasedItems);
                if (bestMessage.isEmpty()) {
                    logger.warn("⚠️ No suitable quests, skipping turn.");
                    break;
                }
                MessageResponse.Message task = bestMessage.get();
                logger.info("🏆 Best task: '{}' | Reward: {} | Probability: {} | Expires in: {}", task.getMessage(), task.getReward(), task.getProbability(), task.getExpiresIn());
                SolveMessageResponse response = messageService.solveMessage(game.getGameId(), task.getAdId());
                game.setGold(response.getGold());
                game.setLives(response.getLives());
                game.setScore(response.getScore());
                logger.info("LIVES: {}, SCORE: {}, GOLD: {}, HIGH SCORE: {}, MESSAGE: {}", game.getLives(), game.getScore(), game.getGold(), response.getHighScore(), response.getMessage());
                if (game.getLives() <= 0) {
                    logger.info("💀 Game Over! Final Score: {}, Gold: {}, High Score: {}", game.getScore(), game.getGold(), game.getHighScore());
                    break;
                }
                if (game.getScore() >= 1000) {
                    logger.info("🎉 Victory! Reached target score: {}, Gold: {}, Lives: {}", game.getScore(), game.getGold(), game.getLives());
                    break;
                }
                logger.info("🔄 Continuing game: Score: {}, Gold: {}, Lives: {}", game.getScore(), game.getGold(), game.getLives());
            } catch (IllegalStateException e) {
                logger.error("❌ Game Over detected");
                break;
            }
        }
        try {
            reputationService.getReputation(game.getGameId());
        } catch (IllegalStateException ignored) {
        }
        return game;
    }

    public GameResponse startGame() {
        String url = BASE_URL + "/game/start";
        logger.info("🚀 Starting new game: POST {}", url);

        try {
            return restTemplate.postForObject(url, null, GameResponse.class);
        } catch (HttpClientErrorException e) {
            logger.error("❌ HTTP error while starting game: {} {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw e;
        }
    }
}
