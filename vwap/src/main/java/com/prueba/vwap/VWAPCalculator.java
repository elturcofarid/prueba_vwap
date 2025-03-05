package com.prueba.vwap;

import com.prueba.vwap.model.MarketTick;

public class VWAPCalculator {
    
    private final String instrument;
    private double bidTotalProduct = 0;
    private double askTotalProduct = 0;
    private long bidTotalVolume = 0;
    private long askTotalVolume = 0;

    public VWAPCalculator(String instrument) {
        this.instrument = instrument;
    }

    public synchronized void updateVWAP(MarketTick tick) {
        bidTotalProduct += tick.getBidPrice() * tick.getBidVolume();
        bidTotalVolume += tick.getBidVolume();
        
        askTotalProduct += tick.getAskPrice() * tick.getAskVolume();
        askTotalVolume += tick.getAskVolume();
        
        double bidVWAP = bidTotalVolume > 0 ? bidTotalProduct / bidTotalVolume : 0;
        double askVWAP = askTotalVolume > 0 ? askTotalProduct / askTotalVolume : 0;

        System.out.printf("Instrument: %s, Market: %s, Bid VWAP: %.4f, Ask VWAP: %.4f%n", 
                        instrument, tick.getMarketPlace(), bidVWAP, askVWAP);
    }
}
