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
 * A game piece that has an immutable location corresponding to its location
 * within the game's board. Hotels can be incorporated or unincorporated.
 * @author Nicholas Maltbie
 */
public class Hotel extends Product implements Serializable
{
    /**Start of the capital letters in the ASCII character table**/
    public static final int CAPITAL_LETTER_START = 65;
    
    /**The hotel's location within the grid.*/
    private Location location;
    
    /**
     * Creates an incorporated hotel that is controlled by owner.
     * @param location immutable position of the hotel within the grid.
     * @param owner Corporation that has rights to this hotel. If this is null,
     * the hotel will be considered unincorporated.
     */
    public Hotel(Location location, Corporation owner)
    {
        super(owner, "H_" + (char)(CAPITAL_LETTER_START+location.getRow()) + (location.getCol()+1));
        this.location = location;
    }
    
    /**
     * Creates an unincorporated hotel (the owner is a null reference).
     * @param location immutable position of the hotel within the grid.
     */
    public Hotel(Location location)
    {
        this(location, null);
        this.location = location;
    }
    
    /**
     * Gets a string representation of the hotel's location as a character then
     * an integer to represent the row and column.
     * @return Returns the hotel's location as a String, ex: "A1".
     */
    public String getLocationText()
    {
        if(location.getCol() < 10)
            return "" + (char)(CAPITAL_LETTER_START+location.getRow()) + (location.getCol()+1);
        return "" + (char)(CAPITAL_LETTER_START+location.getRow()) + (location.getCol()+1);
    }
    
    /**
     * Checks if this hotel is incorporated. If incorporated, it will have
     * an owner, if not, its owner will be a null reference.
     * @return Returns if the hotel has an owner (is incorporated).
     */
    public boolean isIncorporated()
    {
        return this.getOwner() != null;
    }
    
    /**
     * Gets the current owner of this Hotel. If the hotel is unincorporated, 
     * this will return null. 
     * @return the current owner of this Hotel.
     */
    @Override
    public Corporation getOwner()
    {
        return super.getOwner();
    }
    
    /**
     * Sets a new owner of the hotel/will incorporate an unincorporated hotel
     * @param company new owner.
     * @return Returns if the hotel was unincorporated.
     */
    @Override
    public boolean exchangeOwner(Corporation company)
    {
        boolean incorporated = isIncorporated();
        super.exchangeOwner(company);
        return incorporated;
    }
    
    
    /**
     * Disincorporates the hotel (sets it's owner to null)
     * @return Returns if the hotel was incorporated. if it is incorporated,
     * returns true, if the hotel was unincorporated, it will return false;
     */
    public boolean removeOwner()
    {
        return super.exchangeOwner(null);
    }
    
    /**
     * Gets this hotel's location within the game grid
     * @return Returns the location field
     */
    public Location getLocation()
    {
        return location;
    }   
    
    @Override
    public String toString()
    {
        if(!isIncorporated())
            return super.toString() + "_U";
        return super.toString() + "_" + getOwner().getCorporateName().charAt(0);
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof Hotel)
        {
            Hotel otherHotel = (Hotel) other;
            return super.equals(other) && getLocation().equals(otherHotel.getLocation());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 53 * hash + Objects.hashCode(this.location);
        return hash;
    }
}
