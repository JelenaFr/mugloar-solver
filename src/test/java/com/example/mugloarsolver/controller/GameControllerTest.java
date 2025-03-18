package com.example.mugloarsolver.controller;

import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.service.GameService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class GameControllerTest {
    private MockMvc mockMvc;

    @Mock
    private GameService gameService;

    @InjectMocks
    private GameController gameController;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        mockMvc = MockMvcBuilders.standaloneSetup(gameController).build();
    }

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
                .andExpect( jsonPath("$.lives").value(3))
                .andExpect( jsonPath("$.gold").value(100))
                .andExpect( jsonPath("$.score").value(0));

        verify(gameService, times(1)).autoPlay();
    }

}
