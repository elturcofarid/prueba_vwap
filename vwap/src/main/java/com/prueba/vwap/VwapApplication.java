package com.prueba.vwap;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.prueba.vwap.model.MarketTick;

public class VwapApplication {

    private static final int UPDATE_INTERVAL_MS = 1000;
    private static final int THREAD_POOL_SIZE = 3;
    
    public static void main(String[] args) {
        MarketDataProvider marketDataProvider = new MarketDataProvider();
        
        String[] instruments = {"OIL", "GOLD", "GRAIN"};
        String[] marketPlaces = {"NYSE", "NASDAQ", "AMEX"};
        
        Map<String, VWAPCalculator> vwapCalculators = initializeCalculators(
        instruments, marketPlaces, marketDataProvider);
        
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        simulateMarketData(executorService, instruments, marketPlaces, marketDataProvider);
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
        }));
    }
    
    private static Map<String, VWAPCalculator> initializeCalculators(
            String[] instruments, 
            String[] marketPlaces, 
            MarketDataProvider provider) {
    
        Map<String, VWAPCalculator> calculators = new ConcurrentHashMap<>();
        for (String instrument : instruments) {
            VWAPCalculator calculator = new VWAPCalculator(instrument);
            calculators.put(instrument, calculator);
                provider.subscribe(instrument, calculator::updateVWAP);
        }
        return calculators;
    }
    
    private static void simulateMarketData(
            ScheduledExecutorService executor, 
            String[] instruments, 
            String[] marketPlaces, 
            MarketDataProvider provider) {
      
        Random random = new Random();
        
        for (String instrument : instruments) {
            for (String market : marketPlaces) {
                final String finalInstrument = instrument;
                final String finalMarket = market;
                
                executor.scheduleAtFixedRate(() -> {
                    try {
                        double bidPrice = random.nextDouble(100, 150);
                        double askPrice = bidPrice + random.nextDouble(0.1, 5);
                        
                        MarketTick tick = new MarketTick(
                            finalInstrument,
                            finalMarket,
                            bidPrice,
                            askPrice,
                            random.nextInt(100, 1000),
                            random.nextInt(100, 1000)
                        );
                        provider.simulateMarketUpdate(tick);
                    } catch (Exception e) {
                        System.err.println("Error generating market data: " + e.getMessage());
                    }
                }, 0, UPDATE_INTERVAL_MS, TimeUnit.MILLISECONDS);
            }
        }
    }
    
}