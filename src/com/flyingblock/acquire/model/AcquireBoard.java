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

import java.util.ArrayList;
import java.util.List;

/**
 * A grid that represents the available space for hotel chains to grow within
 * the game bounds.
 * @author Nick_Pro
 */
public class AcquireBoard extends Board<Hotel>
{
    /**
     * Constructs a game board of the specified dimensions.
     * @param rows Number of rows that the grid will have.
     * @param cols Number of columns that the board will have.
     */
    public AcquireBoard(int rows, int cols) 
    {
        super(rows, cols);
    }
    
    /**
     * Incorporates a hotel found within the grid.
     * @param row Row of the hotel to incorporate.
     * @param col Column of the hotel to incorporate.
     * @param corporation New owning company for the corporation.
     * @return Returns if the space is filled or not. If true, the action completed
     * successfully, if false, no hotel was found at the specified location.
     */
    public boolean incorporateHotel(int row, int col, Corporation corporation)
    {
        if(this.isEmpty(row, col))
            return false;
        this.get(row, col).exchangeOwner(corporation);
        return true;
    }
    
    /**
     * Disincorporates a hotel found within the grid.
     * @param row Row of the hotel to disincorporate.
     * @param col Column of the hotel to disincorporate.
     * @return Returns if the space is filled or not. If true, the action completed
     * successfully, if false, no hotel was found at the specified location.
     */
    public boolean unincorporateHotel(int row, int col)
    {
        if(this.isEmpty(row, col))
            return false;
        this.get(row, col).removeOwner();
        return true;
    }
    
    /**
     * Gets a blob of orthogonally connected locations within the grid starting
     * starting from Location start and going out in a flood pattern.
     * @param start
     * @return 
     */
    public List<Location> getBlob(Location start)
    {
        List<Location> locs = new ArrayList<>();
        //This has been changed
        System.out.println("git rid of me");
    }
    
}
