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
     * @param move1 Hotel that was moved.
     * @param index1 Index that move1 was placed in.
     * @param move2 Second hotel that was moved.
     * @param index2 Index that move2 was placed in.
     */
    public void piecesSwapped(Hotel move1, int index1, Hotel move2, int index2);
    /**
     * Called when a player places a piece within the board. This Location should
     * match up with the placed hotel's location unless specified otherwise.
     * @param placed Hotel placed.
     * @param loc Location in the board that it was played.
     */
    public void piecePlaced(Hotel placed, Location loc);
}
