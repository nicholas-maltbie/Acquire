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
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import com.flyingblock.acquire.view.HandListener;
import com.flyingblock.acquire.view.HotelView;
import java.awt.Color;
import java.awt.event.MouseEvent;

/**
 * A class that allows a player to manage the pieces in his/her hand.
 * @author Nicholas Maltbie
 */
public class HandManager implements HandListener
{
    /**
     * The index the last piece was pulled from.
     */
    private int current;
    /**
     * Piece being held.
     */
    private Hotel piece;
    /**
     * Investor who's hand is being edited.
     */
    private Investor investor;
    /**
     * Game GUI.
     */
    private GameView view;
    
    /**
     * Constructs a hand manager that allows a player to move pieces in his hand.
     * Must be activated with the method start() as it starts out off.
     * @param view GUI component that has th hand.
     * @param investor Player who is editing his hand.
     */
    public HandManager(GameView view, Investor investor)
    {
        this.view = view;
        this.investor = investor;
        current = -1;
    }
    
    /**
     * Starts the manager and allows the player to edit his/her hand.
     */
    public void start()
    {
        view.addHandListener(this);
    }
    
    /**
     * Stops the listener discontinuing the player's ability to edit his/her hand.
     */
    public void stop()
    {
        view.removeHandListener(this);
    }
    
    @Override
    public void handClicked(int index, MouseEvent event) 
    {
        //hace nada
    }

    @Override
    public void handPressed(int index, MouseEvent event)
    {
        if(investor.getFromHand(index) != null)
        {
            piece = investor.removeFromHand(index);
            current = index;
            view.setFollowingComponent(new HotelView(piece, Color.BLACK
                    , "TIMES NEW ROMAN"));
            view.setFollowSize(view.getHandPieceBounds());
        }
    }

    @Override
    public void handReleased(int index, MouseEvent event) 
    {
        
    }
    
}
