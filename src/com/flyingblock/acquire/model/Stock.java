/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.model;

import java.io.Serializable;
import java.util.Objects;

/**
 * Stocks correspond to a corporation. Each corporation has a finite number of
 * stocks. Each stock has a market trade in value determined by MarketValue
 * @author Nicholas Maltbie
 */
public class Stock extends Product implements Serializable
{
    /**
     * The Market value for a stock. This is from a game element to give certain
     * stocks a higher starting value. Trade in values for stocks can be 
     * determined from the MarketValue class.
     */
    private MarketValue value;
    
    /**
     * Creates a stock for the company that will have a MarketValue value.
     * @param company Corporation distributing stocks.
     * @param value The MarketLevel for the stock.
     */
    public Stock(Corporation company, MarketValue value)
    {
        super(company, company.getCorporateName() + " stock");
        this.value = value;
    }
    
    /**
     * Gets the MarketValue for this particular stock.
     * @return Returns the value field
     */
    public MarketValue getMarketValue()
    {
        return value;
    }
    
    /**
     * Gets the current trade in value of this stock.
     * @return Returns an integer amount of dollars that this stock is currently
     * worth in its game.
     */
    public int getTradeInValue()
    {
        if(getOwner().isEstablished())
            return MarketValue.getStockValue(MarketValue.getLevel(value, this.getOwner()));
        return 0;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Stock)
        {
            Stock otherStock = (Stock) other;
            return otherStock.sameOwner(this) &&
                    otherStock.getMarketValue().equals(this.getMarketValue());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 37 * hash + Objects.hashCode(this.value);
        return hash;
    }
}
