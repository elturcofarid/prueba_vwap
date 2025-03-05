package com.prueba.vwap;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import com.prueba.vwap.model.MarketTick;

/**
 * Aplicación principal que simula un sistema de cálculo VWAP para múltiples instrumentos financieros
 * en diferentes mercados. Implementa un sistema de publicación-suscripción para procesar
 * actualizaciones de mercado en tiempo real.
 */
public class VwapApplication {

    // Intervalo de actualización para la simulación de datos de mercado (1 segundo)
    private static final int UPDATE_INTERVAL_MS = 1000;
    // Tamaño del pool de hilos para procesamiento paralelo
    private static final int THREAD_POOL_SIZE = 3;
    
    public static void main(String[] args) {
        // Inicializa el proveedor de datos de mercado que maneja las suscripciones
        MarketDataProvider marketDataProvider = new MarketDataProvider();
        
        // Define los instrumentos financieros y mercados a simular
        String[] instruments = {"OIL", "GOLD", "GRAIN"};
        String[] marketPlaces = {"NYSE", "NASDAQ", "AMEX"};
        
        // Inicializa las calculadoras VWAP para cada instrumento
        Map<String, VWAPCalculator> vwapCalculators = initializeCalculators(
        instruments, marketPlaces, marketDataProvider);
        
        // Configura el executor service para la simulación de datos de mercado
        ScheduledExecutorService executorService = Executors.newScheduledThreadPool(THREAD_POOL_SIZE);
        simulateMarketData(executorService, instruments, marketPlaces, marketDataProvider);
        
        // Configura el hook de apagado para una terminación limpia
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
    
    /**
     * Inicializa las calculadoras VWAP para cada instrumento y las registra
     * con el proveedor de datos de mercado.
     *
     * @param instruments Array de instrumentos financieros
     * @param marketPlaces Array de mercados
     * @param provider Proveedor de datos de mercado
     * @return Mapa de calculadoras VWAP indexadas por instrumento
     */
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
    
    /**
     * Simula la generación de datos de mercado para cada combinación de
     * instrumento y mercado a intervalos regulares.
     *
     * @param executor Servicio de ejecución programada
     * @param instruments Array de instrumentos financieros
     * @param marketPlaces Array de mercados
     * @param provider Proveedor de datos de mercado
     */
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
                        // Genera precios bid-ask realistas (bid siempre menor que ask)
                        double bidPrice = random.nextDouble(100, 150);
                        double askPrice = bidPrice + random.nextDouble(0.1, 5);
                        
                        // Crea y publica un nuevo tick de mercado
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