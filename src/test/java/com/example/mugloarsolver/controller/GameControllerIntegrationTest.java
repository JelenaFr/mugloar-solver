package com.example.mugloarsolver.controller;

import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.service.GameService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(GameController.class)
class GameControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private GameService gameService;

    @Test
    void startGame_ShouldReturnGameResponse() throws Exception {
        // Given
        GameResponse mockResponse = new GameResponse();
        mockResponse.setGameId("testGameId");
        mockResponse.setLives(3);
        mockResponse.setGold(100);
        mockResponse.setScore(0);

        when(gameService.autoPlay()).thenReturn(mockResponse);
        // When & Then
        mockMvc.perform(post("/game/start")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.gameId").value("testGameId"))
                .andExpect(jsonPath("$.lives").value(3))
                .andExpect(jsonPath("$.gold").value(100))
                .andExpect(jsonPath("$.score").value(0));


        verify(gameService, times(1)).autoPlay();
    }
}