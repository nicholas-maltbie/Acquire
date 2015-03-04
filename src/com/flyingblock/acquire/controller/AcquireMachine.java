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

import com.flyingblock.acquire.controller.AcquireMachine.GameState;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Machine that runs an acquire game.
 * @author Nicholas Maltbie
 */
public class AcquireMachine extends AbstractFSM<GameState>
{
    /**
     * Game board.
     */
    private AcquireBoard board;
    /**
     * Companies.
     */
    private List<Corporation> companies;
    /**
     * Draw pile for hotels.
     */
    private HotelMarket market;
    /**
     * Opponent players (just the model, assuming they are computers).
     */
    private List<Investor> opponents;
    /**
     * Human player playing the game.
     */
    private Investor player;
    /**
     * Current player playing, 0 is human, 1 to whatever are other players.
     */
    private int currentPlayer;
    /**
     * View for the acquire game.
     */
    private GameView view;
    
    /**
     * Constructs a single player acquire game that will take a human player
     * and computer players with given setup.
     * @param board Board to use in game.
     * @param corporations Corporations for the game.
     * @param market Draw pile for hotels.
     * @param investors Computer players.
     * @param player Human player.
     * @param start Starting player (0 is current player while 1 through
     * investors.size() will be an investor).
     */
    public AcquireMachine(AcquireBoard board, List<Corporation> corporations, 
            HotelMarket market, List<Investor> investors, Investor player, int
            start)
    {
        super(stateMap, GameState.SETUP);
        this.currentPlayer = start;
        this.board = board;
        this.companies = corporations;
        this.market = market;
        this.opponents = investors;
        this.player = player;
        view = new GameView(board, companies, player, opponents,
                "TIMES NEW ROMAN", true);
    }

    @Override
    protected void stateStarted(GameState state) {
        switch(state)
        {
            case SETUP:
                market.shuffle();
                for(Investor player : opponents)
                    player.drawFromDeck(market);
                player.drawFromDeck(market);
                view.setupAndDisplayGUI();
                view.update();
                this.setState(GameState.PLAYER_TURN);
                break;
            case PLAYER_TURN:
                HumanPlayerFSM turn = new HumanPlayerFSM(board, market, 
                    player, companies, view);
                break;
        }
    }

    @Override
    protected void stateEnded(GameState state)
    {
            
    }
    
    /**
     * All the different states the game can be in.
     */
    public static enum GameState {SETUP, PLAYER_TURN, END};
    /**
     * The map that decides which states can go to which.
     */
    public static final Map<GameState, EnumSet<GameState>> stateMap;
    
    /**
     * Setup for the stateMap.
     */
    static{
        stateMap = new HashMap<>();
        stateMap.put(GameState.SETUP, EnumSet.of(GameState.PLAYER_TURN));
        stateMap.put(GameState.PLAYER_TURN, EnumSet.of(GameState.PLAYER_TURN,
                GameState.END));
        stateMap.put(GameState.END, EnumSet.of(GameState.PLAYER_TURN));
    }
}
