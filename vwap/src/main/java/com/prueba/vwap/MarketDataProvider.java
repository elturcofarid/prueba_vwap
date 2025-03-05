package com.prueba.vwap;

import com.prueba.vwap.model.MarketTick;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;

public class MarketDataProvider {

    private final Map<String, List<Consumer<MarketTick>>> subscribers = new ConcurrentHashMap<>();
    
    public void subscribe(String instrument, Consumer<MarketTick> subscriber) {
        subscribers.computeIfAbsent(instrument, k -> new CopyOnWriteArrayList<>())
                .add(subscriber);
    }
    
    public void simulateMarketUpdate(MarketTick tick) {
        List<Consumer<MarketTick>> instrumentSubscribers = subscribers.get(tick.getInstrument());
        
        if (instrumentSubscribers != null) {
            instrumentSubscribers.forEach(subscriber -> {
                try {
                    subscriber.accept(tick);
                } catch (Exception e) {
                    System.err.println("Error processing market update: " + e.getMessage());
                }
            });
        }
    }
}
