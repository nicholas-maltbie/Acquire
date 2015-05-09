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
import java.util.ArrayList;
import java.util.List;

/**
 * A grid that represents the available space for hotel chains to grow within
 * the game bounds.
 * @author Nicholas Maltbie
 */
public class AcquireBoard extends Board<Hotel> implements Serializable
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
     * Constructs a default board size 9x12, A-I, 1-12.
     */
    public AcquireBoard()
    {
        this(9,12);
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
     * @param row Row of the specified hotel.
     * @param col Column of the specified hotel.
     * @return Returns if specified hotel is incorporated.
     */
    public boolean isIncorporated(int row, int col)
    {
        return get(row,col) != null && get(row,col).isIncorporated();
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
     * @param row Row of starting location
     * @param col Column of starting location
     * @return Returns an array containing all hotels of the affected area
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
    
    /**
     * Will return all the companies that have incorporated hotels in a 
     * blob of the interconnected hotels starting with a defined location.
     * @param row Row of the defined location.
     * @param col Col of the defined location.
     * @return Returns the companies that own at least one company in 
     * the blob.
     */
    public List<Corporation> getCorporationsInBlob(int row, int col)
    {
        List<Corporation> companies = new ArrayList<>();
        for(Location loc: getBlob(row, col))
        {
            if(!isEmpty(loc.getRow(), loc.getCol()))
            {
                Corporation owner = get(loc.getRow(), loc.getCol()).getOwner();
                if(owner != null && !companies.contains(owner))
                    companies.add(owner);
            }
        }
        return companies;
    }
    
    /**
     * Gets all the companies that own at least one hotel within the board.
     * @return Returns the companies that have a presence within the board.
     */
    public List<Corporation> getCompaniesOnBoard()
    {
        List<Corporation> companies = new ArrayList<>();
        for(int row = 0; row < getNumRows(); row++)
        {
            for(int col = 0; col < getNumCols(); col++)
            {
                if(!isEmpty(row,col))
                {
                    Corporation owner = get(row, col).getOwner();
                    if(owner != null && !companies.contains(owner))
                        companies.add(owner);
                }
            }
        }
        return companies;
    }
    
    /**
     * Gets a copy of this acquire board
     * @return Returns a copy.
     */
    public AcquireBoard copy()
    {
        AcquireBoard board = new AcquireBoard(getNumRows(), getNumCols());
        for(int r = 0; r < getNumRows(); r++)
            for(int c = 0; c < getNumCols(); c++)
                board.set(r, c, get(r,c));
        return board;
    }
}
