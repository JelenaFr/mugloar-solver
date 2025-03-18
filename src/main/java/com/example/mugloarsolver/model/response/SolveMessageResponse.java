package com.example.mugloarsolver.model.response;

import lombok.Data;

@Data
public class SolveMessageResponse {
    private boolean success;
    private int lives;
    private int gold;
    private int score;
    private int highScore;
    private int turn;
    private String message;
}