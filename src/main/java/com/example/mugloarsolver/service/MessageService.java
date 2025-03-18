package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.ShopItem;
import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.MessageResponse;
import com.example.mugloarsolver.model.response.SolveMessageResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

@Service
public class MessageService {
    private static final Logger logger = LoggerFactory.getLogger(GameService.class);
    private static final String BASE_URL = "https://dragonsofmugloar.com/api/v2";
    private final RestTemplate restTemplate;
    private final RiskCalculatorService riskCalculatorService;

    public MessageService(RestTemplate restTemplate, RiskCalculatorService riskCalculatorService) {
        this.restTemplate = restTemplate;
        this.riskCalculatorService = riskCalculatorService;
    }

    public List<MessageResponse.Message> getMessages(String gameId) {
        String url = BASE_URL + "/" + gameId + "/messages";
        logger.info("📡 Requesting messages: GET {}", url);
        ResponseEntity<List<MessageResponse.Message>> response = restTemplate.exchange(url, HttpMethod.GET, null, new ParameterizedTypeReference<>() {
        });
        return response.getBody();
    }

    public SolveMessageResponse solveMessage(String gameId, String adId) {
        String url = BASE_URL + "/" + gameId + "/solve/" + adId.replaceAll("=+$", "");
        logger.info("📡 Sending request: POST {}", url);
        return restTemplate.postForObject(url, null, SolveMessageResponse.class);
    }

    public Optional<MessageResponse.Message> chooseBestMessage(List<MessageResponse.Message> messages, GameResponse game, List<ShopItem> purchasedItems) {
        if (messages == null || messages.isEmpty()) {
            logger.warn("⚠️ No available messages for gameId={}", game.getGameId());
            return Optional.empty();
        }
        return messages.stream()
                .map(this::decodeMessage)
                .filter(msg -> msg.getProbability() != null)
                .filter(msg -> isValidProbability(msg.getProbability()))
                .filter(msg -> !msg.getProbability().equals("Hmmm...")
                        && !msg.getProbability().equals("Gamble")
                        && !msg.getProbability().equals("Suicide mission")
                        && !msg.getProbability().equals("Rather detrimental")
                        && !(msg.getProbability().equals("Playing with fire")
                        && !hasProtection(purchasedItems)))
                .filter(msg -> isNumeric(msg.getReward()))
                .max(Comparator.comparingInt(msg -> riskCalculatorService.safeParseInt(msg.getReward()) - riskCalculatorService.calculateRisk(msg, game, purchasedItems)))
                .map(this::decodeMessage);
    }

    private boolean isValidProbability(String probability) {
        return probability.equals("Sure thing")
                || probability.equals("Piece of cake")
                || probability.equals("Walk in the park")
                || probability.equals("Quite likely")
                || probability.equals("Risky")
                || probability.equals("Rather detrimental")
                || probability.equals("Suicide mission")
                || probability.equals("Playing with fire")
                || probability.equals("Hmmm...")
                || probability.equals("Gamble");
    }

    private boolean isNumeric(String str) {
        return str != null && str.matches("\\d+");
    }

    private String decodeBase64IfNeeded(String value) {
        if (value == null || value.isBlank())
            return value;
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(value);
            return new String(decodedBytes, StandardCharsets.UTF_8);
        } catch (IllegalArgumentException e) {
            return value;
        }
    }

    private MessageResponse.Message decodeMessage(MessageResponse.Message message) {
        if (message == null)
            return null;
        if (Boolean.TRUE.equals(message.getEncrypted())) {
            message.setMessage(decodeBase64IfNeeded(message.getMessage()));
            message.setProbability(decodeBase64IfNeeded(message.getProbability()));
            message.setAdId(decodeBase64IfNeeded(message.getAdId()));
        }
        return message;
    }

    private boolean hasProtection(List<ShopItem> purchasedItems) {
        return purchasedItems.stream().anyMatch(item -> item.getName().contains("Plating") || item.getName().contains("Potion"));
    }
}
