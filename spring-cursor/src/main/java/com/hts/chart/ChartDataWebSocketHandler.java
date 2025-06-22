package com.hts.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hts.stock.StockService;
import com.hts.websocket.dto.FrontendWebSocketRequest;
import com.hts.websocket.dto.FrontendWebSocketResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.TextWebSocketHandler;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.concurrent.CopyOnWriteArrayList;

@Component
public class ChartDataWebSocketHandler extends TextWebSocketHandler {

    private static final Logger logger = LoggerFactory.getLogger(ChartDataWebSocketHandler.class);
    
    private final CopyOnWriteArrayList<WebSocketSession> sessions = new CopyOnWriteArrayList<>();
    private final ObjectMapper objectMapper;
    private final StockService stockService;
    private final ChartService chartService;

    public ChartDataWebSocketHandler(ObjectMapper objectMapper, StockService stockService, ChartService chartService) {
        this.objectMapper = objectMapper;
        this.stockService = stockService;
        this.chartService = chartService;
    }

    @Override
    public void afterConnectionEstablished(WebSocketSession session) throws Exception {
        sessions.add(session);
        logger.info("WebSocket connection established. Total connections: {}", sessions.size());
        
        // Send welcome message
        FrontendWebSocketResponse welcomeResponse = FrontendWebSocketResponse.success(
            "CONNECTION_ESTABLISHED", 
            null, 
            "WebSocket connection established successfully"
        );
        sendMessage(session, welcomeResponse);
        
        super.afterConnectionEstablished(session);
    }

    @Override
    public void afterConnectionClosed(WebSocketSession session, CloseStatus status) throws Exception {
        sessions.remove(session);
        logger.info("WebSocket connection closed. Total connections: {}", sessions.size());
        super.afterConnectionClosed(session, status);
    }

    @Override
    protected void handleTextMessage(WebSocketSession session, TextMessage message) throws Exception {
        try {
            String payload = message.getPayload();
            logger.debug("Received message from client: {}", payload);
            
            // Parse the incoming request
            FrontendWebSocketRequest request = objectMapper.readValue(payload, FrontendWebSocketRequest.class);
            
            // Process the request based on its type
            FrontendWebSocketResponse response = processRequest(request, session);
            
            // Send response back to the client
            sendMessage(session, response);
            
        } catch (Exception e) {
            logger.error("Error processing WebSocket message", e);
            
            // Send error response
            FrontendWebSocketResponse errorResponse = FrontendWebSocketResponse.error(
                "ERROR", 
                null, 
                "Failed to process request: " + e.getMessage()
            );
            sendMessage(session, errorResponse);
        }
    }

    private FrontendWebSocketResponse processRequest(FrontendWebSocketRequest request, WebSocketSession session) {
        String requestId = request.getRequestId();
        String type = request.getType();
        
        try {
            switch (type) {
                case FrontendWebSocketRequest.RequestTypes.GET_STOCK_DATA:
                    return handleGetStockData(request, requestId);
                    
                case FrontendWebSocketRequest.RequestTypes.GET_CHART_DATA:
                    return handleGetChartData(request, requestId);
                    
                case FrontendWebSocketRequest.RequestTypes.SUBSCRIBE_TO_TICKER:
                    return handleSubscribeToTicker(request, requestId, session);
                    
                case FrontendWebSocketRequest.RequestTypes.UNSUBSCRIBE_FROM_TICKER:
                    return handleUnsubscribeFromTicker(request, requestId, session);
                    
                case FrontendWebSocketRequest.RequestTypes.PING:
                    return FrontendWebSocketResponse.pong(requestId);
                    
                default:
                    return FrontendWebSocketResponse.error(
                        FrontendWebSocketResponse.ResponseTypes.ERROR,
                        requestId,
                        "Unknown request type: " + type
                    );
            }
        } catch (Exception e) {
            logger.error("Error processing request type: {}", type, e);
            return FrontendWebSocketResponse.error(
                FrontendWebSocketResponse.ResponseTypes.ERROR,
                requestId,
                "Internal server error: " + e.getMessage()
            );
        }
    }

    private FrontendWebSocketResponse handleGetStockData(FrontendWebSocketRequest request, String requestId) {
        try {
            FrontendWebSocketRequest.StockDataRequest stockRequest = objectMapper.convertValue(
                request.getPayload(), 
                FrontendWebSocketRequest.StockDataRequest.class
            );
            
            if (stockRequest.getTicker() == null || stockRequest.getTicker().trim().isEmpty()) {
                return FrontendWebSocketResponse.error(
                    FrontendWebSocketResponse.ResponseTypes.ERROR,
                    requestId,
                    "Ticker is required"
                );
            }
            
            var stock = stockService.getStockByTicker(stockRequest.getTicker());
            return FrontendWebSocketResponse.success(
                FrontendWebSocketResponse.ResponseTypes.STOCK_DATA,
                requestId,
                stock
            );
            
        } catch (Exception e) {
            return FrontendWebSocketResponse.error(
                FrontendWebSocketResponse.ResponseTypes.ERROR,
                requestId,
                "Failed to get stock data: " + e.getMessage()
            );
        }
    }

    private FrontendWebSocketResponse handleGetChartData(FrontendWebSocketRequest request, String requestId) {
        try {
            FrontendWebSocketRequest.ChartDataRequest chartRequest = objectMapper.convertValue(
                request.getPayload(), 
                FrontendWebSocketRequest.ChartDataRequest.class
            );
            
            if (chartRequest.getTicker() == null || chartRequest.getTicker().trim().isEmpty()) {
                return FrontendWebSocketResponse.error(
                    FrontendWebSocketResponse.ResponseTypes.ERROR,
                    requestId,
                    "Ticker is required"
                );
            }
            
            // Get chart data from the service with default time range (last 24 hours)
            LocalDateTime to = LocalDateTime.now();
            LocalDateTime from = to.minusHours(24);
            ChartInterval interval = chartRequest.getInterval() != null ? 
                ChartInterval.valueOf(chartRequest.getInterval()) : ChartInterval.ONE_MINUTE;
            
            var chartData = chartService.getChartData(chartRequest.getTicker(), from, to, interval);
            
            return FrontendWebSocketResponse.success(
                FrontendWebSocketResponse.ResponseTypes.CHART_DATA,
                requestId,
                chartData
            );
            
        } catch (Exception e) {
            return FrontendWebSocketResponse.error(
                FrontendWebSocketResponse.ResponseTypes.ERROR,
                requestId,
                "Failed to get chart data: " + e.getMessage()
            );
        }
    }

    private FrontendWebSocketResponse handleSubscribeToTicker(FrontendWebSocketRequest request, String requestId, WebSocketSession session) {
        try {
            FrontendWebSocketRequest.StockDataRequest subscribeRequest = objectMapper.convertValue(
                request.getPayload(), 
                FrontendWebSocketRequest.StockDataRequest.class
            );
            
            if (subscribeRequest.getTicker() == null || subscribeRequest.getTicker().trim().isEmpty()) {
                return FrontendWebSocketResponse.error(
                    FrontendWebSocketResponse.ResponseTypes.ERROR,
                    requestId,
                    "Ticker is required for subscription"
                );
            }
            
            // Here you would implement the subscription logic
            // For now, we'll just return a success response
            logger.info("Client {} subscribed to ticker: {}", session.getId(), subscribeRequest.getTicker());
            
            return FrontendWebSocketResponse.success(
                FrontendWebSocketResponse.ResponseTypes.SUBSCRIPTION_CONFIRMED,
                requestId,
                "Successfully subscribed to " + subscribeRequest.getTicker()
            );
            
        } catch (Exception e) {
            return FrontendWebSocketResponse.error(
                FrontendWebSocketResponse.ResponseTypes.ERROR,
                requestId,
                "Failed to subscribe: " + e.getMessage()
            );
        }
    }

    private FrontendWebSocketResponse handleUnsubscribeFromTicker(FrontendWebSocketRequest request, String requestId, WebSocketSession session) {
        try {
            FrontendWebSocketRequest.StockDataRequest unsubscribeRequest = objectMapper.convertValue(
                request.getPayload(), 
                FrontendWebSocketRequest.StockDataRequest.class
            );
            
            if (unsubscribeRequest.getTicker() == null || unsubscribeRequest.getTicker().trim().isEmpty()) {
                return FrontendWebSocketResponse.error(
                    FrontendWebSocketResponse.ResponseTypes.ERROR,
                    requestId,
                    "Ticker is required for unsubscription"
                );
            }
            
            // Here you would implement the unsubscription logic
            logger.info("Client {} unsubscribed from ticker: {}", session.getId(), unsubscribeRequest.getTicker());
            
            return FrontendWebSocketResponse.success(
                FrontendWebSocketResponse.ResponseTypes.UNSUBSCRIPTION_CONFIRMED,
                requestId,
                "Successfully unsubscribed from " + unsubscribeRequest.getTicker()
            );
            
        } catch (Exception e) {
            return FrontendWebSocketResponse.error(
                FrontendWebSocketResponse.ResponseTypes.ERROR,
                requestId,
                "Failed to unsubscribe: " + e.getMessage()
            );
        }
    }

    private void sendMessage(WebSocketSession session, FrontendWebSocketResponse response) {
        try {
            String jsonResponse = objectMapper.writeValueAsString(response);
            session.sendMessage(new TextMessage(jsonResponse));
        } catch (IOException e) {
            logger.error("Failed to send message to client", e);
        }
    }

    public void broadcast(String message) {
        FrontendWebSocketResponse broadcastResponse = FrontendWebSocketResponse.success(
            "BROADCAST",
            null,
            message
        );
        
        for (WebSocketSession session : sessions) {
            if (session.isOpen()) {
                sendMessage(session, broadcastResponse);
            }
        }
    }
} 