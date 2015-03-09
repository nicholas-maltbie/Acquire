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

import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;

/**
 * An interface that listens to player actions form the PieceManager class.
 * @author Nicholas Maltbie
 */
public interface PlayerListener
{
    /**
     * Called when a player swaps pieces within his or her hand.
     * @param move1
     * @param index1
     * @param move2
     * @param index2 
     */
    public void piecesSwapped(Hotel move1, int index1, Hotel move2, int index2);
    /**
     * Called when a player places a piece within the board.
     * @param placed
     * @param loc 
     */
    public void piecePlaced(Hotel placed, Location loc);
}
