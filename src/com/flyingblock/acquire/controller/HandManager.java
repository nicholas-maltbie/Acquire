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
import java.awt.event.MouseListener;

/**
 * A class that allows a player to manage the pieces in his/her hand.
 * @author Nicholas Maltbie
 */
public class HandManager implements HandListener, MouseListener
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
        view.addMouseListener(this);
    }
    
    /**
     * Stops the listener discontinuing the player's ability to edit his/her hand.
     */
    public void stop()
    {
        view.removeHandListener(this);
        view.addMouseListener(this);
    }
    
    /**
     * Moves the piece in new location to the original position of the held
     * piece and then puts the held piece in the new location. If there is no
     * @param newLocation Location of the piece to switch to. If the current
     * index is passed, it will put the piece back in its location.
     */
    private void switchPlaces(int newLocation)
    {
        if(piece != null)
        {
            Hotel move = investor.removeFromHand(newLocation);
            investor.setInHand(current, move);
            investor.setInHand(newLocation, piece);
            piece = null;
        }
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
            System.out.println("red");
            piece = investor.removeFromHand(index);
            current = index;
            view.setFollowingComponent(new HotelView(piece, Color.BLACK
                    , "TIMES NEW ROMAN"));
            view.setFollowSize(view.getHandPieceBounds());
            view.startFollowing();
            view.update();
            view.repaint();
        }
    }
    
    @Override
    public void handReleased(int index, MouseEvent event) 
    {
        switchPlaces(index);
        view.stopFollowing();
        view.removeFollowingComponent();
        view.update();
        view.repaint();
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        switchPlaces(current);
        view.stopFollowing();
        view.removeFollowingComponent();
        view.update();
        view.repaint();
    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        switchPlaces(current);
        view.stopFollowing();
        view.removeFollowingComponent();
        view.update();
        view.repaint();
    }
    
}
