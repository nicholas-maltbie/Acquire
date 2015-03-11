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
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.view.BoardListener;
import com.flyingblock.acquire.view.FollowMouseListener;
import com.flyingblock.acquire.view.GameView;
import com.flyingblock.acquire.view.HandListener;
import com.flyingblock.acquire.view.HotelView;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.List;

/**
 * A class that allows a player to manage the pieces in his/her hand.
 * @author Nicholas Maltbie
 */
public class PieceManager implements HandListener, MouseListener, 
        FollowMouseListener, BoardListener
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
     * Whether or not to return dropped piece to hand.
     */
    private boolean returnToHand = true;
    /**
     * Board that the pieces are held on.
     */
    private AcquireBoard board;
    /**
     * Things listening to the actions of this instance.
     */
    private final List<PlayerListener> listeners;
    
    /**
     * Constructs a hand manager that allows a player to move pieces in his hand 
     * and place pieces within the board.
     * Must be activated with the method start() as it starts out off.
     * @param view GUI component that has th hand.
     * @param investor Player who is editing his hand.
     * @param board Board that holds pieces.
     */
    public PieceManager(GameView view, Investor investor, AcquireBoard board)
    {
        this.listeners = new ArrayList<>();
        this.view = view;
        this.investor = investor;
        this.board = board;
        current = -1;
    }
    
    /**
     * Adds a player listener to the list of instances listening to the actions
     * of this class.
     * @param listener Player Listener to add to the listeners.
     */
    public void addPlayerListener(PlayerListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Removes a player listener from the current list of listeners.
     * @param listener Player Listener to remove from the listeners.
     */
    public void removePlayerListener(PlayerListener listener)
    {
        listeners.remove(listener);
    }
    
    /**
     * Starts the manager and allows the player to edit his/her hand.
     */
    public void start()
    {
        view.addHandListener(this);
        view.addBoardListener(this);
        view.addMouseListener(this);
        view.addFollowMouseListener(this);
    }
    
    /**
     * Stops the listener discontinuing the player's ability to edit his/her hand.
     */
    public void stop()
    {
        view.removeHandListener(this);
        view.removeBoardListener(this);
        view.removeMouseListener(this);
        view.removeFollowMouseListener(this);
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
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.piecesSwapped(investor.getFromHand(current), current,
                            investor.getFromHand(newLocation), newLocation);
                });
            }
        }
        view.stopFollowing();
        view.removeFollowingComponent();
        view.update();
        view.repaint();
    }
    
    /**
     * Returns the currently held piece to the player's hand.
     */
    public void returnPiece()
    {
        switchPlaces(current);
    }
    
    /**
     * Set whether or not the manager will return pieces to the player's
     * hand if he or she either lets go of the piece or exits the bounds.
     * @param returnToHand 
     */
    public void setReturnToHand(boolean returnToHand)
    {
        this.returnToHand = returnToHand;
    }
    
    /**
     * Gets whether the manager is returning the dropped pieces to the player's
     * hand.
     * @return Returns the returnToHand field.
     */
    public boolean returningToHand()
    {
        return returnToHand;
    }
    
    /**
     * Gets whether the player is holding a piece.
     * @return Returns if the player is dragging, or has dropped and not
     * returned, a .
     */
    public boolean isHoldingPiece()
    {
        return piece != null;
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
            if(piece != null)
                returnPiece();
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
        if(view.mustDrag())
            returnPiece();
    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e)
    {
        if(returnToHand)
            returnPiece();
    }

    @Override
    public void finishedMove(Point target)
    {
        
    }

    @Override
    public void exitedBounds(Point component)
    {
        if(returnToHand)
            returnPiece();        
    }

    @Override
    public void buttonClicked(Location button, MouseEvent event)
    {
        
    }

    @Override
    public void buttonPressed(Location button, MouseEvent event)
    {
        
    }

    @Override
    public void buttonRelease(Location button, MouseEvent event)
    {
        if(piece != null)
        {
            board.set(button.getRow(), button.getCol(), piece);
            view.stopFollowing();
            view.removeFollowingComponent();
            view.update();
            view.repaint();
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) ->{
                    listener.piecePlaced(piece, button);
                });
            }
            piece = null;
        }
        else if(returnToHand)
            returnPiece();
    }
    
}
