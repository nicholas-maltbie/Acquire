/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.controller;

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;

/**
 * A class that contains rules for the acquire board game and is implemented
 * as a utility class.
 * @author Nicholas Maltbie
 */
public class AcquireRules 
{
    /**
     * The size that a corporation needs to be in order to be safe.
     */
    public static final int SAFE_CORPORATION_SIZE = 11;
    
    /**
     * Checks if a piece can be played. A piece would not be able to be played
     * it connected two or more safe corporations.
     * @param hotel Hotel to check.
     * @param board Board to check validity of.
     * @return Returns if the piece can be played.
     */
    public static boolean canPieceBePlayed(Hotel hotel, AcquireBoard board)
    {
        Location loc = hotel.getLocation();
        board.set(loc.getRow(), loc.getCol(), hotel);
        int numSafe = 0;
        for(Corporation c : board.getCorporationsInBlob(loc.getRow(), loc.getCol()))
            if(c.getNumberOfHotels() >= SAFE_CORPORATION_SIZE)
               numSafe++;
        
        board.remove(loc.getRow(), loc.getCol());
        return numSafe <= 1;
    }
}
