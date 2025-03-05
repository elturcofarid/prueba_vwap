package com.prueba.vwap.model;

import java.util.Objects;

public final class MarketTick {

    private final String instrument;
    private final String marketPlace;
    private final double bidPrice;
    private final double askPrice;
    private final int bidVolume;
    private final int askVolume;

    public MarketTick(String instrument, String marketPlace, 
                    double bidPrice, double askPrice, 
                    int bidVolume, int askVolume) {

        if (instrument == null || instrument.isBlank()) {
            throw new IllegalArgumentException("Instrumento inválido");
        }
        if (bidPrice <= 0 || askPrice <= 0) {
            throw new IllegalArgumentException("Precios deben ser positivos");
        }
        
        this.instrument = instrument;
        this.marketPlace = marketPlace;
        this.bidPrice = bidPrice;
        this.askPrice = askPrice;
        this.bidVolume = bidVolume;
        this.askVolume = askVolume;
    }

    public String getInstrument() {
        return instrument;
    }

    public String getMarketPlace() {
        return marketPlace;
    }

    public double getBidPrice() {
        return bidPrice;
    }


    public double getAskPrice() {
        return askPrice;
    }


    public int getBidVolume() {
        return bidVolume;
    }


    public int getAskVolume() {
        return askVolume;
    }

    @Override
public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MarketTick)) return false;
    MarketTick that = (MarketTick) o;
    return Double.compare(that.bidPrice, bidPrice) == 0 &&
           Double.compare(that.askPrice, askPrice) == 0 &&
           bidVolume == that.bidVolume &&
           askVolume == that.askVolume &&
           instrument.equals(that.instrument) &&
           marketPlace.equals(that.marketPlace);
}

@Override
public int hashCode() {
    return Objects.hash(instrument, marketPlace, bidPrice, askPrice, bidVolume, askVolume);
}



    @Override
    public String toString() {
        return String.format("Tick[%s-%s | Bid: %.2f@%d | Ask: %.2f@%d]", 
            instrument, marketPlace, bidPrice, bidVolume, askPrice, askVolume);
    }
}