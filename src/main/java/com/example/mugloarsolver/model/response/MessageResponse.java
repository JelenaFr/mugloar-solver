package com.example.mugloarsolver.model.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
public class MessageResponse {
    private List<Message> messages;

    @Data
    public static class Message {
        private String adId;
        private String message;
        private String reward;
        private int expiresIn;
        private String probability;
        @JsonProperty("encrypted")
        private Integer encrypted;
    }
}
