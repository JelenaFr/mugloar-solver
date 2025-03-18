package com.example.mugloarsolver.model.response;

import lombok.Data;

@Data
public class ShopResponse {
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private String message;
}