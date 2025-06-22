// This is the WebSocket client for Korea Investment & Securities
package com.hts.websocket;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.hts.websocket.dto.RealTimeTradeData;
import com.hts.websocket.dto.WebSocketRequest;
import jakarta.websocket.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.math.BigDecimal;
import java.net.URI;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Service
public class KoreaInvestmentWebSocketClient {

    private static final Logger logger = LoggerFactory.getLogger(KoreaInvestmentWebSocketClient.class);
    private static final String WEBSOCKET_URL = "wss://openapi.koreainvestment.com:9443/websocket";

    @Value("${korea.investment.approval.key:}")
    private String approvalKey;

    private final ObjectMapper objectMapper;
    private Session webSocketSession;
    private final ScheduledExecutorService reconnectExecutor;
    private String currentTicker;
    private boolean isConnected = false;

    public KoreaInvestmentWebSocketClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.reconnectExecutor = Executors.newSingleThreadScheduledExecutor();
    }

    public void connect(String ticker) {
        this.currentTicker = ticker;

        if (approvalKey == null || approvalKey.trim().isEmpty()) {
            logger.error("Approval key is not configured. Please set korea.investment.approval.key in application properties.");
            return;
        }

        try {
            WebSocketContainer container = ContainerProvider.getWebSocketContainer();
            webSocketSession = container.connectToServer(new WebSocketClientEndpoint(), new URI(WEBSOCKET_URL));

        } catch (Exception e) {
            logger.error("Failed to create WebSocket connection", e);
        }
    }

    public void disconnect() {
        if (webSocketSession != null && webSocketSession.isOpen()) {
            try {
                webSocketSession.close();
            } catch (IOException e) {
                logger.error("Error closing WebSocket session", e);
            }
        }
        isConnected = false;
        currentTicker = null;
    }

    public boolean isConnected() {
        return isConnected && webSocketSession != null && webSocketSession.isOpen();
    }

    private void processMessage(String message) {
        try {
            // Split message by '|' and get the third segment (index 2)
            String[] segments = message.split("\\|");
            if (segments.length < 3) {
                logger.warn("Invalid message format: {}", message);
                return;
            }

            String dataSegment = segments[2];
            String[] fields = dataSegment.split("\\^");

            if (fields.length < 32) {
                logger.warn("Insufficient data fields: {}", dataSegment);
                return;
            }

            // Parse fields according to the specification
            String ticker = fields[0];
            String tradeTime = fields[1];
            BigDecimal currentPrice = new BigDecimal(fields[2]);
            BigDecimal open = new BigDecimal(fields[7]);
            BigDecimal high = new BigDecimal(fields[8]);
            BigDecimal low = new BigDecimal(fields[9]);
            Long volume = Long.parseLong(fields[12]);
            Long totalValue = Long.parseLong(fields[14]);
            String bizDate = fields[31];

            // Create DTO
            RealTimeTradeData tradeData = new RealTimeTradeData(
                ticker, tradeTime, currentPrice, open, high, low, volume, totalValue, bizDate
            );

            // Print to console in JSON format
            String jsonOutput = objectMapper.writeValueAsString(tradeData);
            System.out.println(jsonOutput);

            // TODO: Save to Redis or database in the future
            // saveToDatabase(tradeData);

        } catch (Exception e) {
            logger.error("Failed to process WebSocket message: {}", message, e);
        }
    }

    private void scheduleReconnection() {
        logger.info("Scheduling reconnection in 5 seconds...");
        reconnectExecutor.schedule(() -> {
            if (currentTicker != null && !isConnected()) {
                logger.info("Attempting to reconnect for ticker: {}", currentTicker);
                connect(currentTicker);
            }
        }, 5, TimeUnit.SECONDS);
    }

    // Inner class for WebSocket endpoint
    @ClientEndpoint
    private class WebSocketClientEndpoint {

        @OnOpen
        public void onOpen(Session session) {
            logger.info("WebSocket connection opened for ticker: {}", currentTicker);
            webSocketSession = session;
            isConnected = true;

            // Send subscription request
            WebSocketRequest request = new WebSocketRequest(approvalKey, currentTicker);
            try {
                String requestJson = objectMapper.writeValueAsString(request);
                logger.info("Sending subscription request: {}", requestJson);
                session.getBasicRemote().sendText(requestJson);
            } catch (IOException e) {
                logger.error("Failed to send WebSocket request", e);
            }
        }

        @OnMessage
        public void onMessage(String message) {
            logger.debug("Received WebSocket message: {}", message);
            processMessage(message);
        }

        @OnClose
        public void onClose(Session session, CloseReason closeReason) {
            logger.warn("WebSocket connection closed. Reason: {}", closeReason);
            isConnected = false;

            // Schedule reconnection if it was not a normal close
            if (!closeReason.getCloseCode().equals(CloseReason.CloseCodes.NORMAL_CLOSURE) && currentTicker != null) {
                scheduleReconnection();
            }
        }

        @OnError
        public void onError(Session session, Throwable throwable) {
            logger.error("WebSocket error occurred", throwable);
            isConnected = false;
        }
    }

    // TODO: Implement database saving functionality
    /*
    private void saveToDatabase(RealTimeTradeData tradeData) {
        // Implementation for saving to database or Redis
        // This will be implemented in the future
    }
    */
} 