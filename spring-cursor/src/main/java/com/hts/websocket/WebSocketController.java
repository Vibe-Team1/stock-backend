package com.hts.websocket;

import com.hts.websocket.KoreaInvestmentWebSocketClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/websocket")
public class WebSocketController {

    private final KoreaInvestmentWebSocketClient webSocketClient;

    public WebSocketController(KoreaInvestmentWebSocketClient webSocketClient) {
        this.webSocketClient = webSocketClient;
    }

    @PostMapping("/connect/{ticker}")
    public ResponseEntity<Map<String, Object>> connectToTicker(@PathVariable String ticker) {
        Map<String, Object> response = new HashMap<>();

        try {
            webSocketClient.connect(ticker);
            response.put("success", true);
            response.put("message", "WebSocket connection established for ticker: " + ticker);
            response.put("ticker", ticker);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to connect to WebSocket: " + e.getMessage());
            response.put("ticker", ticker);
            return ResponseEntity.badRequest().body(response);
        }
    }

    @PostMapping("/disconnect")
    public ResponseEntity<Map<String, Object>> disconnect() {
        Map<String, Object> response = new HashMap<>();

        try {
            webSocketClient.disconnect();
            response.put("success", true);
            response.put("message", "WebSocket connection disconnected");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Failed to disconnect WebSocket: " + e.getMessage());
            return ResponseEntity.badRequest().body(response);
        }
    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getConnectionStatus() {
        Map<String, Object> response = new HashMap<>();

        boolean isConnected = webSocketClient.isConnected();
        response.put("connected", isConnected);
        response.put("message", isConnected ? "WebSocket is connected" : "WebSocket is disconnected");

        return ResponseEntity.ok(response);
    }

    @GetMapping("/test")
    public ResponseEntity<Map<String, Object>> testWebSocket() {
        Map<String, Object> response = new HashMap<>();

        response.put("message", "WebSocket test endpoint");
        response.put("instructions", "Use POST /api/websocket/connect/{ticker} to connect to a specific ticker");
        response.put("example", "POST /api/websocket/connect/005930");
        response.put("note", "Make sure to set korea.investment.approval.key in application properties");

        return ResponseEntity.ok(response);
    }
} 