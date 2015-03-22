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
import com.sun.glass.events.WindowEvent;
import java.awt.AWTEvent;
import java.awt.Color;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * Machine that runs an acquire game.
 * @author Nicholas Maltbie
 */
public class AcquireMachine extends AbstractFSM<GameState> implements MergerPanelListener
{
    public static final int END_CORPORATION_SIZE = 41;
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
     * saves if the program is waiting.
     */
    private boolean waiting;
    
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
            case END:
                //Hand out majority and minority bonuses
                for(Corporation c : companies)
                    handOutMajorityAndMinorityBonuses(c);
                //cash in player stocks
                cashInStocks(player);
                for(Investor p : opponents)
                    cashInStocks(p);
                //organize players by money.
                List<Investor> allPlayers = new ArrayList<>(Arrays.asList(player));
                allPlayers.addAll(opponents);
                allPlayers.sort(new InvestorComparator());
                
                String endMessage = "";
                for(int i = 0; i < allPlayers.size(); i++)
                    endMessage += (i+1) + ". " + allPlayers.get(i).toString()+"\n";
                endMessage += "\nPlay again?";
                JFrame frame = new JFrame();
                boolean again = JOptionPane.showConfirmDialog(
                        frame,
                        endMessage,
                        allPlayers.get(0) + " wins",
                        JOptionPane.YES_NO_OPTION) == 0;
                
                view.dispose();
                if(again)
                {
                    board.fill(null);
                    for(Corporation c : companies)
                        if(c.isEstablished())
                            c.dissolve();
                    market = new HotelMarket(board);
                    
                    for(int h = 0; h < player.getHandSize(); h++)
                        player.removeFromHand(h);
                    player.addMoney(6000 - player.getMoney());
                    for(Investor i : opponents)
                    {
                        for(int h = 0; h < i.getHandSize(); h++)
                            i.removeFromHand(h);
                        i.addMoney(6000 - i.getMoney());
                    }
                    currentPlayer = (int)(Math.random()*(opponents.size()+1));
                    setState(GameState.SETUP);
                }
                else
                {
                    System.exit(0);
                }
                break;
        }
    }

    @Override
    protected void stateEnded(GameState state)
    {
        
    }

    @Override
    public void finished(Corporation parent, Corporation child, int kept, int sold, int traded)
    {
        waiting = false;
    }
    
    public void turnEnded(Investor player)
    {
        final GameState nextTurn;
        //Decide if game is over
        List<Corporation> established = board.getCompaniesOnBoard();
        boolean over = false;
        if(!established.isEmpty())
        {
            boolean full = false;
            boolean allSafe = true;
            for(Corporation c : established)
            {
                if(c.getNumberOfHotels() >= END_CORPORATION_SIZE)
                    full = true;
                if(c.getNumberOfHotels() < HumanPlayerFSM.SAFE_CORPORATION_SIZE)
                    allSafe = false;
            }
            over = full || allSafe;
        }
        
        if(!over)
        {
            //if not, go to next turn
            currentPlayer++;
            currentPlayer %= opponents.size()+1;
            //Make a choice who's going next, human or AI
            if(currentPlayer == 0)
                nextTurn = GameState.HUMAN_TURN;
            else
                nextTurn = GameState.AI_TURN;
        }
        else
            nextTurn = GameState.END;
        
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
    
    /**
     * Gives out the majority and minority bonuses to players in the game.
     * @param c Company to hand out majority and minority bonuses.
     */
    public void handOutMajorityAndMinorityBonuses(Corporation c)
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
    }
    
    /**
     * Cashes in all the player's stocks and gives him or her the money.
     * @param p Player to cash in stocks of.
     */
    public void cashInStocks(Investor p)
    {
        while(p.getNumStocks() > 0)
            p.addMoney(p.removeStock(0).getTradeInValue());
    }
    
    /**
     * Does a merger including, handing out majority and minority bonuses, 
     * prompts human and AI players to make decisions about their stocks, and 
     * dissolves the consumed corporations.
     * @param parent Company that comes out on top.
     * @param food Company(ies) that are consumed by the parent.
     */
    public void handelMerger(Corporation parent, List<Corporation> food)
    {
        int newSize = parent.getNumberOfHotels() + 1;
        for(Corporation c : food)
            newSize += c.getNumberOfHotels();
        parent.incorporateRegoin();
        //Hand out majority and minory bonus for all food eaten.
        for(Corporation c : food)
        {
            handOutMajorityAndMinorityBonuses(c);
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
            final Corporation next;
            if(largest.size() > 1)
                next = turn.choseCorporationFromList(largest, "Choose corporation to be eaten first", "Merger Choice");
            else
                next = largest.get(0);
            food.remove(next);
            
            //Players chose what to do with their stocks from the food.
            List<Investor> shareHolders = new ArrayList<>();
            int checked = 0;
            int index = currentPlayer;
            //get relevant investors.
            while(checked < getNumPlayers())
            {
                if(getPlayer(index).getStocks(next) > 0)
                    shareHolders.add(getPlayer(index));
                index = (index+1) % getNumPlayers();
                checked++;
            }
            //have relevant investors take actions.
            for(Investor shareHolder :shareHolders)
            {
                //start waiting
                waiting = true;
                //start a new thread to allow the player to make his/her choices.
                new java.util.Timer().schedule( 
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            getPlayerMerger(parent, next, shareHolder);
                        }
                    }, 0);
                //wait until the player has finished his or her action.
                long time = System.currentTimeMillis();
                //System.out.println("started Waiting");
                while(waiting)
                    try {
                        Thread.sleep(10l);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(AcquireMachine.class.getName()).log(Level.SEVERE, null, ex);
                    }
                //System.out.println("finished Waiting " + (System.currentTimeMillis() - time) + "ms elapsed");
            }
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
    
    /**
     * Gets a player from the current in game players.
     * @param index Index of player to get. Index 0 is the human player
     * and greater numbers will yield AI players. 
     * @return Returns the player at index index.
     */
    public Investor getPlayer(int index)
    {
        if(index == 0)
            return player;
        else
            return opponents.get(index-1);
    }
    
    /**
     * Gets a player merger action where he or she will have to hold, trade in,
     * or sell all of his or her stocks in the child company.
     * @param parent Company that eats the child.
     * @param child Company being merged.
     * @param shareholder Investor taking a merger action.
     */
    public void getPlayerMerger(Corporation parent, Corporation child, Investor shareholder)
    {
        //human player
        if(shareholder.equals(player))
        {
            MergerPanel panel = new MergerPanel(parent, child, shareholder, Color.BLACK,
            Color.WHITE);
            panel.setupAndDisplayGUI(new Rectangle(100,100,700,700));
            panel.addListener(this);
        }
        //AI player
        else
        {
            
        }
    }
    
    /**
     * Gets the number of AI and human players in the game.
     * @return Returns the number of players in the game.
     */
    public int getNumPlayers()
    {
        return opponents.size() + 1;
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
        stateMap.put(GameState.END, EnumSet.of(GameState.SETUP));
    }
}
