package com.example.mugloarsolver.controller;

import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.service.GameService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class GameController {
    private static final Logger logger = LoggerFactory.getLogger(GameController.class);
    private final GameService gameService;

    public GameController(GameService gameService) {
        this.gameService = gameService;
    }

    @PostMapping("game/start")
    public GameResponse startGame() {
        logger.info("Controller method startGame() invoked!");
        return gameService.autoPlay();
    }


}