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
import java.awt.event.MouseEvent;

/**
 * Classes will be able to listen to a BoardView and have access to user input
 * from the Board View.
 * @author Nicholas Maltbie
 */
public interface BoardListener 
{
    /**
     * Called whenever the user clicks within the grid.
     * @param button Location on the grid in which the mouse was clicked.
     * @param event Mouse event that holds data about the action.
     */
    public void buttonClicked(Location button, MouseEvent event);
    /**
     * Called whenever the user presses within the grid.
     * @param button Location on the grid in which the mouse was pressed.
     * @param event Mouse event that holds data about the action.
     */
    public void buttonPressed(Location button, MouseEvent event);
    /**
     * Called whenever the user releases a button within the grid.
     * @param button Location on the grid in which the mouse was released.
     * @param event Mouse event that holds data about the action.
     */
    public void buttonRelease(Location button, MouseEvent event);
}
