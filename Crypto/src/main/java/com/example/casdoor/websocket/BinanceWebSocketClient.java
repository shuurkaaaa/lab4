package com.example.casdoor.websocket;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import java.net.URI;
import java.time.LocalTime;
import java.util.concurrent.Executors;

@ClientEndpoint
@Component
public class BinanceWebSocketClient {

    private static final String BINANCE_WS_URL =
            "wss://stream.binance.com:9443/stream?streams=btcusdt@trade/ethusdt@trade/xrpusdt@trade/dogeusdt@trade";

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @EventListener(ApplicationReadyEvent.class)
    public void start() {
        Executors.newSingleThreadExecutor().submit(() -> {
            try {
                WebSocketContainer container = ContainerProvider.getWebSocketContainer();
                container.connectToServer(this, new URI(BINANCE_WS_URL));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }

    @OnMessage
    public void onMessage(String message) {
        try {
            JSONObject json = new JSONObject(message);
            JSONObject data = json.getJSONObject("data");

            String fullSymbol = data.getString("s"); // e.g. BTCUSDT
            String symbol = fullSymbol.replace("USDT", ""); // BTC, ETH etc
            double price = data.getDouble("p"); // last price
            String timestamp = LocalTime.now().withNano(0).toString();

            JSONObject payload = new JSONObject();
            payload.put("symbol", symbol);
            payload.put("price", price);
            payload.put("time", timestamp);

            messagingTemplate.convertAndSend("/topic/crypto", payload.toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
