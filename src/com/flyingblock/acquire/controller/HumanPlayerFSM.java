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

import com.flyingblock.acquire.controller.HumanPlayerFSM.TurnState;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;

/**
 * This is a State Machine that will run a human player's turn allowing him/her 
 * to edit the board according to the rules of the game.
 * @author Nicholas Maltbie
 */
public class HumanPlayerFSM extends AbstractFSM<TurnState>
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
        super(stateMap, TurnState.PLACE_PIECE);
        this.board = board;
        this.deck = deck;
        this.player = player;
        this.companies = companies;
        this.view = view;
        PieceManager manager = new PieceManager(view, player, board);
        manager.start();
        manager.setReturnToHand(false);
        view.setDrag(true);
    }

    @Override
    protected void stateStarted(TurnState state) 
    {
        
    }

    @Override
    protected void stateEnded(TurnState state) 
    {
        
    }
    
    /**
     * Turn states.
     */
    public static enum TurnState {PLACE_PIECE, MERGER, BUY_STOCKS, END_TURN};
    /**
     * State map for the turn states.
     */
    public static final HashMap<TurnState, EnumSet<TurnState>> stateMap;
    /**
     * setups turn states.
     */
    static {
        stateMap = new HashMap<>();
        stateMap.put(TurnState.PLACE_PIECE,
                EnumSet.of(TurnState.MERGER, TurnState.BUY_STOCKS));
        stateMap.put(TurnState.MERGER, EnumSet.of(TurnState.BUY_STOCKS));
        stateMap.put(TurnState.BUY_STOCKS, EnumSet.of(TurnState.END_TURN));
        stateMap.put(TurnState.END_TURN, EnumSet.noneOf(TurnState.class));
    }
}
