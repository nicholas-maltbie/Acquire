package com.flyingblock.acquire.model;

import java.util.Objects;

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
    private Hotel hq;
    
    /**
     * Constructs a new Corporation.
     * @param name Company's name.
     * @param region The game board that this corporation participates in.
     * @param value The value of the corporation's products.
     */
    public Corporation(String name, AcquireBoard region, MarketValue value)
    {
        this.corporateName = name;
        this.gameBoard = region;
        this.value = value;
    }
    
    /**
     * Sets the headquarters and establishes this company within its region.
     * @param loc Location of Headquarters, this should be where the second 
     * hotel in its chain is played.
     */
    public void setHeadquarters(Location loc)
    {
        hq = new Hotel(loc, this);
    }
    
    /**
     * Returns the location of the corporation's headquarters within its region.
     * @return Returns a Location for the headquarters, this will return null
     * if the company has not been established yet.
     */
    public Location getHeadquarters()
    {
        if(hq == null)
            return null;
        return hq.getLocation();
    }
    
    /**
     * Returns if the corporation has been established within its region yet.
     * @return Returns if the corporation has been established within its region yet.
     */
    public boolean isEstablished()
    {
        return hq != null;
    }
    
    /**
     * Dissolves the corporation within its region. This will remove its
     * headquarters and it will not longer appear as being established. This will
     * additionally unincorporate any hotels connected to the headquarters.
     */
    public void dissolve()
    {
        for(Location l : gameBoard.getBlob(hq.getLocation().getRow(), hq.getLocation().getCol()))
        {
            Hotel h = gameBoard.get(l.getRow(), l.getCol());
            if(h.isIncorporated() && h.getOwner().equals(this))
                h.removeOwner();
        }
        hq = null;
    }
    
    /**
     * Gets the number of hotels owned by this company on the game board.
     * @return Returns the number of hotels that this corporation owns
     * within its game board.
     */
    public int getNumberOfHotels()
    {
        if(hq == null)
            return 0;
        return gameBoard.getBlob(hq.getLocation().getRow(), hq.getLocation().getCol()).size();
    }
    
    /**
     * This method will, if the corporation is established, incorporate all the
     * unincorporated hotels connected to its headquarters.
     */
    public void incorporateRegoin()
    {
        System.out.println(hq.getLocation());
        if(isEstablished())
            for(Location l : gameBoard.getBlob(hq.getLocation().getRow(), hq.getLocation().getCol()))
            {
                Hotel h = gameBoard.get(l.getRow(), l.getCol());
                if(!h.isIncorporated())
                    h.exchangeOwner(this);
            }
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
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Corporation)
        {
            Corporation c = (Corporation) other;
            return c.corporateName.equals(corporateName) && c.hq.equals(hq) &&
                    c.value.equals(value) && c.gameBoard.equals(gameBoard);
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 79 * hash + Objects.hashCode(this.corporateName);
        hash = 79 * hash + Objects.hashCode(this.value);
        hash = 79 * hash + Objects.hashCode(this.gameBoard);
        hash = 79 * hash + Objects.hashCode(this.hq);
        return hash;
    }
    
    @Override
    public String toString()
    {
        if(isEstablished())
            return corporateName + " of value " + value.toString() + " at " + hq.toString();
        return corporateName + " of value " + value.toString() + " (not established)";
    }
}
