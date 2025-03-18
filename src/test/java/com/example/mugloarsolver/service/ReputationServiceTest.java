package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.response.ReputationResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

public class ReputationServiceTest {
    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private ReputationService reputationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getReputation_ShouldReturnReputationResponse() {
        // Given
        ReputationResponse mockResponse = new ReputationResponse();
        mockResponse.setPeople(10);
        mockResponse.setState(5);
        mockResponse.setUnderworld(2);

        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenReturn(mockResponse);

        // When
        ReputationResponse response = reputationService.getReputation("12345");

        // Then
        assertNotNull(response);
        assertEquals(10, response.getPeople());
        assertEquals(5, response.getState());
        assertEquals(2, response.getUnderworld());
        verify(restTemplate, times(1)).postForObject(anyString(), eq(null), eq(ReputationResponse.class));
    }

    @Test
    void getReputation_ShouldThrowException_WhenGameOver() {
        // Given
        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.GONE, "Game Over"));

        // When & Then
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () ->
                reputationService.getReputation("12345")
        );
        assertEquals("Game Over", thrown.getMessage());
    }

    @Test
    void getReputation_ShouldThrowHttpClientErrorException_WhenOtherHttpErrorOccurs() {
        // Given
        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenThrow(new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Bad Request"));

        // When & Then
        HttpClientErrorException thrown = assertThrows(HttpClientErrorException.class, () ->
                reputationService.getReputation("12345")
        );
        assertEquals(HttpStatus.BAD_REQUEST, thrown.getStatusCode());
    }

    @Test
    void getReputation_ShouldReturnNull_WhenResponseIsNull() {
        // Given
        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenReturn(null);

        // When
        ReputationResponse response = reputationService.getReputation("12345");

        // Then
        assertNull(response);
    }

    @Test
    void getReputation_ShouldThrowRestClientException_WhenRequestFails() {
        // Given
        when(restTemplate.postForObject(anyString(), eq(null), eq(ReputationResponse.class)))
                .thenThrow(new RuntimeException("Connection timeout"));

        // When & Then
        RuntimeException thrown = assertThrows(RuntimeException.class, () ->
                reputationService.getReputation("12345")
        );

        assertEquals("Connection timeout", thrown.getMessage());
    }
}