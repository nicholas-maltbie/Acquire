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

import java.awt.event.MouseEvent;

/**
 * Classes will be able to listen to a HandView and have access to user input
 * from the HandView.
 * @author Nicholas Maltbie
 */
public interface HandListener
{
    /**
     * Called when the player clicks on the hand.
     * @param index Index of piece clicked within the hand.
     * @param event Mouse event that holds data about the action.
     */
    public void handClicked(int index, MouseEvent event);
    /**
     * Called when the player presses on the hand.
     * @param index Index of piece pressed within the hand.
     * @param event Mouse event that holds data about the action.
     */
    public void handPressed(int index, MouseEvent event);
    /**
     * Called when the player releases a click on the hand.
     * @param index Index of piece released within the hand.
     * @param event Mouse event that holds data about the action.
     */
    public void handReleased(int index, MouseEvent event);    
}
