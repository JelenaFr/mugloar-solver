package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.response.ReputationResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;


    @Service
    public class ReputationService {
        private static final Logger logger = LoggerFactory.getLogger(ReputationService.class);
        private static final String BASE_URL = "https://dragonsofmugloar.com/api/v2";
        private final RestTemplate restTemplate;

        public ReputationService(RestTemplate restTemplate) {
            this.restTemplate = restTemplate;
        }

        public ReputationResponse getReputation(String gameId) {
            String url = BASE_URL + "/" + gameId + "/investigate/reputation";
            logger.info("📡 Requesting reputation: POST {}", url);

            try {
                ReputationResponse response = restTemplate.postForObject(url, null, ReputationResponse.class);
                if (response != null) {
                    logger.info("🔄 Reputation: People: {}, State: {}, Underworld: {}",
                            response.getPeople(), response.getState(), response.getUnderworld());
                }
                return response;
            } catch (HttpClientErrorException e) {
                if (e.getStatusCode() == HttpStatus.GONE) {
                    logger.error("❌ Game Over detected while checking reputation.");
                    throw new IllegalStateException("Game Over");
                }
                throw e;
            }
        }
    }
