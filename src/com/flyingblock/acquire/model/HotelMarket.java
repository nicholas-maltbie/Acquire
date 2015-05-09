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
import java.util.Collections;
import java.util.List;
import java.util.Random;

/**
 * This represents the bag of hotels that players draw and put on the board.
 * @author Nicholas Maltbie
 */
public class HotelMarket implements Serializable
{
    public static final long serialVersionUID = 1L;
    /**
     * Seed for shuffling the market.
     */
    private Random random;
    /**
     * Deck of hotels to draw from.
     */
    private List<Hotel> hotels;
    
    /**
     * Constructs a hotel market for the game board board and seeds the random
     * number generation for shuffling the deck to seed.
     * @param board Board of the game.
     * @param seed Seed for the random number generator.
     */
    public HotelMarket(AcquireBoard board, long seed)
    {
        hotels = new ArrayList<>(board.getNumRows() * board.getNumCols());
        for(int row = 0; row < board.getNumRows(); row++)
            for(int col = 0; col < board.getNumCols(); col++)
                hotels.add(new Hotel(new Location(row, col)));
        random = new Random(seed);
    }
    
    /**
     * Constructs a hotel market for the game board board and seeds the random
     * number generation with the current system time.
     * @param board Board of the game.
     */
    public HotelMarket(AcquireBoard board)
    {
        this(board, System.currentTimeMillis());
    }
    
    /**
     * Will reseed the random number generator to a new seed.
     * @param seed New seed for the rng.
     */
    public void reseed(long seed)
    {
        random.setSeed(seed);
    }
    
    /**
     * Shuffles the deck of pieces
     */
    public void shuffle()
    {
        Collections.shuffle(hotels, random);
    }
    
    /**
     * Gets the current number of hotels in the hotel deck.
     * @return Returns the size of the hotel deck.
     */
    public int getSize()
    {
        return hotels.size();
    }
    
    /**
     * Adds a hotel to the current market.
     * @param hotel Hotel to be added to the deck.
     */
    public void addHotel(Hotel hotel)
    {
        hotels.add(hotel);
    }
    
    /**
     * Gets a hotel from the market leaving the market unchanged.
     * @param index Index of the deck to check.
     * @return Returns the hotel found at index.
     */
    public Hotel getHotel(int index)
    {
        return hotels.get(index);
    }
    
    /**
     * Removes a hotel from the deck.
     * @param index Index of the hotel to remove.
     * @return Returns the hotel that was removed.
     */
    public Hotel removeHotel(int index)
    {
        return hotels.remove(index);
    }
    
    /**
     * Removes a hotel from the top of the deck or nothing if the deck is empty.
     * @return Returns the removed hotel.
     */
    public Hotel draw()
    {
        if(getSize() > 0)
            return removeHotel(0);
        return null;
    }
    
}