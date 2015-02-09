package com.flyingblock.acquire.model;

/**
 * A company that can sell/control products. Additionally, they represent a presence
 * in the game by controlling hotel chains and stocks.
 * @author Nicholas Maltbie
 */
public class Corporation 
{
    private String corporateName;
    private MarketValue value;
    private AcquireBoard gameBoard;
    
    /**
     * Gets the number of hotels owned by this company on the game board.
     * @return Returns the number of hotels that this corporation owns
     * within its game board.
     */
    public int getNumberOfHotels()
    {
        
    }
    
    /**
     * Gets the MarketValue for this company's stocks.
     * @return Returns the MarketValue of this company's stocks.
     */
    public MarketValue getMarketValue()
    {
        return value;
    }
    
    /**
     * Gets the corporate name for the corporation.
     * @return Returns the corporateName field
     */
    public String getCorporateName()
    {
        return corporateName;
    }
}
