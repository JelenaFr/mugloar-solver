package com.example.mugloarsolver.service;

import com.example.mugloarsolver.model.response.GameResponse;
import com.example.mugloarsolver.model.response.MessageResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

public class MessageServiceTest {
    @Mock
    private RestTemplate restTemplate;
    @Mock
    private RiskCalculatorService riskCalculatorService;
    @InjectMocks
    private MessageService messageService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getMessages_ShouldReturnListOfMessages() {
        // Given
        MessageResponse.Message message = new MessageResponse.Message();
        message.setMessage("Test message");

        List<MessageResponse.Message> mockMessages = List.of(message);
        ResponseEntity<List<MessageResponse.Message>> mockResponse = ResponseEntity.ok(mockMessages);

        when(restTemplate.exchange(anyString(), any(), eq(null), any(ParameterizedTypeReference.class)))
                .thenReturn(mockResponse);

        // When
        List<MessageResponse.Message> messages = messageService.getMessages("testGameId");

        // Then
        assertNotNull(messages);
        assertEquals(1, messages.size());
        assertEquals("Test message", messages.get(0).getMessage());
    }
    @Test
    void chooseBestMessage_ShouldReturnBestMessage() {
        // Given
        MessageResponse.Message msg1 = new MessageResponse.Message();
        msg1.setReward("50");
        msg1.setProbability("Quite likely");

        MessageResponse.Message msg2 = new MessageResponse.Message();
        msg2.setReward("100");
        msg2.setProbability("Sure thing");

        List<MessageResponse.Message> messages = List.of(msg1, msg2);

        // Mocking risk calculation (если нужно)
        when(riskCalculatorService.safeParseInt("50")).thenReturn(50);
        when(riskCalculatorService.safeParseInt("100")).thenReturn(100);
        when(riskCalculatorService.calculateRisk(any(), any(), any())).thenReturn(10);  // Задаем риск

        // When
        Optional<MessageResponse.Message> bestMessage = messageService.chooseBestMessage(messages, new GameResponse(), List.of());

        // Then
        assertTrue(bestMessage.isPresent());
        assertEquals("100", bestMessage.get().getReward());
    }
}
