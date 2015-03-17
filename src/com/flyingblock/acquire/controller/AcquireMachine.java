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
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

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
     * Player turn that operates a player's actions.
     */
    private HumanPlayerFSM turn;
    
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
                List<Investor> players = new ArrayList<>(opponents);
                players.add(player);
                for(Investor player : players)
                {
                    Hotel h = market.draw();
                    board.set(h.getLocation().getRow(), h.getLocation().getCol(), h);
                    player.drawFromDeck(market);
                }
                currentPlayer = (int)(Math.random()*players.size());
                view.setupAndDisplayGUI();
                view.update();
                this.setState(GameState.HUMAN_TURN);
                break;
            case HUMAN_TURN:
                turn = new HumanPlayerFSM(this, board, market, 
                    player, companies, view);
                turn.start();
                break;
            case AI_TURN:
                //nothing yet
                this.setState(GameState.HUMAN_TURN);
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
    public static enum GameState {SETUP, AI_TURN, HUMAN_TURN, END};
    /**
     * The map that decides which states can go to which.
     */
    public static final Map<GameState, EnumSet<GameState>> stateMap;
    
    /**
     * Setup for the stateMap.
     */
    static{
        stateMap = new HashMap<>();
        stateMap.put(GameState.SETUP, EnumSet.of(GameState.HUMAN_TURN, GameState.AI_TURN));
        stateMap.put(GameState.HUMAN_TURN, EnumSet.of(GameState.HUMAN_TURN, GameState.AI_TURN,
                GameState.END));
        stateMap.put(GameState.AI_TURN, EnumSet.of(GameState.HUMAN_TURN, GameState.AI_TURN));
        stateMap.put(GameState.END, EnumSet.of(GameState.HUMAN_TURN, GameState.AI_TURN));
    }
    
    public void turnEnded(Investor player)
    {
        currentPlayer++;
        currentPlayer %= opponents.size()+1;
        //Make a choice who's going next, human or AI
        final GameState nextTurn;
        if(currentPlayer == 0)
            nextTurn = GameState.HUMAN_TURN;
        else
            nextTurn = GameState.AI_TURN;
        //use delay to avoid filling the stack.
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    //go to next turn
                    setState(nextTurn);
                }
            }, 0);
    }
    
    public void handelMerger(Corporation parent, List<Corporation> food)
    {
        int newSize = parent.getNumberOfHotels() + 1;
        for(Corporation c : food)
            newSize += c.getNumberOfHotels();
        parent.incorporateRegoin();
        //Hand out majority and minory bonus for all food eaten.
        for(Corporation c : food)
        {
            List<Investor> majority = new ArrayList<>();
            if(player.getStocks(c) > 0)
                majority.add(player);
            for(Investor opponent : opponents)
            {
                if(opponent.getStocks(c) > 0)
                {
                    if(majority.isEmpty())
                        majority.add(opponent);
                    else if(opponent.getStocks(c) > majority.get(0).getStocks(c))
                    {
                        majority.clear();
                        majority.add(opponent);
                    }
                    else if(opponent.getStocks(c) == majority.get(0).getStocks(c))
                        majority.add(opponent);
                }
            }
            
            List<Investor> minority = new ArrayList<>();
            if(!majority.contains(player) && player.getStocks(c) > 0)
                minority.add(player);
            for(Investor opponent : opponents)
            {
                if(opponent.getStocks(c) > 0 && !majority.contains(opponent))
                {
                    if(minority.isEmpty())
                        minority.add(opponent);
                    else if(opponent.getStocks(c) > minority.get(0).getStocks(c))
                    {
                        minority.clear();
                        minority.add(opponent);
                    }
                    else if(opponent.getStocks(c) == minority.get(0).getStocks(c))
                        minority.add(opponent);
                }
            }
            int majorityBonus = c.getMajorityBonus();
            int minorityBonus = c.getMinorityBonus();
            if(majority.size() > 1) {
                majorityBonus = (c.getMajorityBonus() + c.getMinorityBonus())/(100*majority.size())*100;
                minorityBonus = 0;
            }
            else if(minority.size() > 1)
                minorityBonus = c.getMinorityBonus()/(100*minority.size())*100;
            else if(minority.isEmpty())
                majorityBonus = c.getMajorityBonus() + c.getMinorityBonus();
            for(Investor p : majority)
                p.addMoney(majorityBonus);
            for(Investor p : minority)
                p.addMoney(minorityBonus);
            System.out.println("Majority: " + majority + ", minority: " + minority);
        }
        //Take merger actions to finalize the trade.
        while(!food.isEmpty())
        {
            //chose the corporation to get consumed (order matters).
            List<Corporation> largest = new ArrayList<>();
            for(Corporation bite : food)
            {
                if(largest.isEmpty())
                    largest.add(bite);
                else if(bite.getNumberOfHotels() > largest.get(0).getNumberOfHotels())
                {
                    largest.clear();
                    largest.add(bite);
                }
                else if(bite.getNumberOfHotels() == largest.get(0).getNumberOfHotels())
                    largest.add(bite);
            }
            Corporation next = largest.get(0);
            if(largest.size() > 1)
                next = turn.choseCorporationFromList(largest, "Choose corporation to be eaten first", "Merger Choice");
            food.remove(next);
            
            //Players chose what to do with their stocks from the food.
            
            //finalize the action
            next.dissolve();
            view.update();
            view.repaint();
            parent.incorporateRegoin();
            try {
                Thread.sleep(100l);
            } catch (InterruptedException ex) {
                Logger.getLogger(AcquireMachine.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        parent.incorporateRegoin();
        view.update();
        view.repaint();
        //tell the turn that the player's have completed their actions.
        turn.mergerComplete();
    }
}
