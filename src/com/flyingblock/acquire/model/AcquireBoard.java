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
     * Constructs a default board size 9x11, A-I, 1-11.
     */
    public AcquireBoard()
    {
        this(9,11);
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
     * Gets if the hotel at location row, col is incorporated or not.
     * Will throw a NullPointerException if there is no hotel at the specified
     * location.
     * @param row Row of the specified hotel.
     * @param col Column of the specified hotel.
     * @return Returns if specified hotel is incorporated.
     */
    public boolean isIncorporated(int row, int col)
    {
        return get(row,col).isIncorporated();
    }
    /**
     * Gets the Corporation that controls the hotel at the specified location.
     * Will throw a NullPointerException if there is no hotel at the specified
     * location.
     * @param row Row of the specified hotel.
     * @param col Column of the specified hotel.
     * @return Returns if specified hotel is incorporated.
     */
    public Corporation getCorporation(int row, int col)
    {
        return get(row,col).getOwner();
    }
    
    /**
     * Gets a blob of orthogonally connected locations within the grid starting
     * starting from Location start and going out in a flood pattern.
     * @param row
     * @param col
     * @return 
     */
    public List<Location> getBlob(int row, int col)
    {
        List<Location> locs = new ArrayList<>();
        if(row < 0 || row >= getNumRows() || col < 0 || col >= getNumCols() ||
                isEmpty(row, col))
            return locs;
        Hotel hotel = remove(row,col);
        locs.add(new Location(row, col));
        locs.addAll(getBlob(row+1, col));
        locs.addAll(getBlob(row-1, col));
        locs.addAll(getBlob(row, col+1));
        locs.addAll(getBlob(row, col-1));
        set(row, col, hotel);
        return locs;
    }
    
}
