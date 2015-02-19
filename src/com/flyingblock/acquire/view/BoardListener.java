/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.view;

import com.flyingblock.acquire.model.Location;

/**
 * Classes will be able to listen to a BoardView and have access to user input
 * from the Board View.
 * @author Nicholas Maltbie
 */
public interface BoardListener 
{
    /**
     * Called whenever the user clicks within the grid.
     * @param button Location on the grid in which the mouse was pressed.
     */
    public void buttonPressed(Location button);
    /**
     * Called whenever the user releases a button within the grid.
     * @param button Location on the grid in which the mouse was released.
     */
    public void buttonRelease(Location button);
    /**
     * Called whenever the mouse enters a button within the grid.
     * @param button Location on the grid in which the mouse was released.
     */
    public void buttonEnter(Location button);
    /**
     * Called whenever the mouse exits a button within the grid.
     * @param button Location on the grid in which the mouse was released.
     */
    public void buttonExit(Location button);
    /**
     * Called whenever the mouse enters the BoardView.
     */
    public void buttonEnter();
    /**
     * Called whenever the mouse exits the BoardView.
     */
    public void buttonExit();
}
