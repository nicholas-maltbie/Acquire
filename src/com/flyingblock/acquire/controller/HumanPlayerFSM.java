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
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import java.util.List;

/**
 * This is a State Machine that will run a human player's turn allowing him/her 
 * to edit the board according to the rules of the game.
 * @author Nicholas Maltbie
 */
public class HumanPlayerFSM 
{
    /**
     * Game board.
     */
    private AcquireBoard board;
    /**
     * Deck to draw tiles from.
     */
    private HotelMarket deck;
    /**
     * Current player that is taking his/her turn.
     */
    private Investor player;
    /**
     * Corporations that have a presence in the game.
     */
    private List<Corporation> companies;
    /**
     * View with all the buttons and fun stuff :D.
     */
    private GameView view;
    
    /**
     * Constructs a human player's turn. Can be started and will take over the
     * GUI and operate the turn.
     * @param board Game board.
     * @param deck Game deck.
     * @param player Player who is taking his/her turn.
     * @param companies Companies.
     * @param view The GUI display that has all the panels.
     */
    public HumanPlayerFSM(AcquireBoard board, HotelMarket deck, Investor player,
            List<Corporation> companies, GameView view)
    {
        this.board = board;
        this.deck = deck;
        this.player = player;
        this.companies = companies;
        this.view = view;
        HandManager manager = new HandManager(view, player);
        manager.start();
    }
}
