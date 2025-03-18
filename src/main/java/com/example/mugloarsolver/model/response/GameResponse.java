package com.example.mugloarsolver.model.response;

import lombok.Data;

@Data
public class GameResponse {
    private String gameId;
    private int lives;
    private int gold;
    private int level;
    private int score;
    private int highScore;
    private int turn;
}
