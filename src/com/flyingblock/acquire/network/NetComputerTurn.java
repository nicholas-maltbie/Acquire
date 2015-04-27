/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.network;

import com.flyingblock.acquire.controller.*;
import com.flyingblock.acquire.computer.Decider;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.network.NetComputerTurn.ComputerState;
import com.flyingblock.acquire.view.GameView;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * FSM to run a computer turn.
 * @author Nicholas Maltbie
 */
public class NetComputerTurn extends AbstractFSM<ComputerState>
{
    /**
     * Decider to make decisions for the investor.
     */
    private Decider decider;
    /**
     * AcquireServer connection, for helping the human see what's happening.
     */
    private AcquireServer acquireServer;
    /**
     * Location used to save data between states.
     */
    private Location loc;
    /**
     * Deck of pieces.
     */
    private HotelMarket deck;
    
    /**
     * Constructs a computer player FSM
     * @param decider Decider to make decisions for the investor.
     * @param machine Server that runs the game.
     * @param deck Deck for game.
     */
    public NetComputerTurn(Decider decider, AcquireServer machine,
            HotelMarket deck)
    {
        super(stateMap, ComputerState.PLAY_PIECE);
        this.decider = decider;
        this.acquireServer = machine;
        this.deck = deck;
    }

    @Override
    protected void stateStarted(ComputerState state)
    {
        acquireServer.updateAllClients();
        switch(state)
        {
            case BUY_STOCKS:
                for(Corporation c : decider.getBoard().getCompaniesOnBoard())
                    c.incorporateRegoin();
                for(int i = 0; i < decider.getPlayer().getHandSize(); i++)
                    if(decider.getPlayer().getFromHand(i) != null && !AcquireRules.canPieceBePlayed(decider.getPlayer().getFromHand(i), decider.getBoard()))
                        decider.getPlayer().removeFromHand(i);
                decider.buyStocks(decider.getBoard().getCompaniesOnBoard());
                decider.getPlayer().drawFromDeck(deck);
                acquireServer.turnEnded(decider.getPlayer());
                break;
            case MERGER:
                List<Corporation> suspects = new ArrayList<>();
                for(Location l : decider.getBoard().getBlob(loc.getRow(), loc.getCol()))
                    if(decider.getBoard().isIncorporated(l.getRow(), l.getCol()) && 
                            !suspects.contains(decider.getBoard().getCorporation(l.getRow(), l.getCol())))
                        suspects.add(decider.getBoard().getCorporation(l.getRow(), l.getCol()));
                List<Corporation> largest = new ArrayList<>();
                for(Corporation suspect : suspects)
                {
                    if(largest.isEmpty())
                        largest.add(suspect);
                    else if(suspect.getNumberOfHotels() > largest.get(0).getNumberOfHotels())
                    {
                        largest.clear();
                        largest.add(suspect);
                    }
                    else if(suspect.getNumberOfHotels() == largest.get(0).getNumberOfHotels())
                        largest.add(suspect);
                }
                
                Corporation parent = largest.get(0);
                if(largest.size() > 1)
                {
                    parent =  decider.choseMergerWinner(largest);
                }
                suspects.remove(parent);
                acquireServer.handelMerger(parent, suspects);
                break;
            case PLAY_PIECE:
                
                Investor investor = decider.getPlayer();
                Hotel play = decider.getPlayOption();
                for(int i = 0; i < investor.getHandSize(); i++)
                    if(investor.getFromHand(i).equals(play))
                        investor.removeFromHand(i);
                
                loc = play.getLocation();
                decider.getBoard().set(loc.getRow(), loc.getCol(), play);
                
                List<Corporation> accessories = decider.getBoard().getCorporationsInBlob(
                        loc.getRow(), loc.getCol());

                final ComputerState nextState;

                //Choose if a merger happens.
                boolean merger = accessories.size() > 1;
                List<Corporation> taken = decider.getBoard().getCompaniesOnBoard();
                List<Corporation> available = new ArrayList<>();
                for(Corporation c : decider.getCompanies())
                    if(!taken.contains(c))
                        available.add(c);
                //Chose if a new company is formed.
                System.out.println(play);
                List<Location> blob = decider.getBoard().getBlob(loc.getRow(), loc.getCol());
                boolean formCompany = available.size() > 0 && blob.size() >= 2;
                for(Location l : blob)
                    if(decider.getBoard().getCorporation(l.getRow(), l.getCol()) != null)
                        formCompany = false;
                if(merger)
                    nextState = ComputerState.MERGER;
                else
                    nextState = ComputerState.BUY_STOCKS;
                
                if(formCompany && !merger)
                {
                    Corporation chosenCompany =  decider.createCorporation(available);
                    chosenCompany.setHeadquarters(play.getLocation());
                    chosenCompany.incorporateRegoin();
                    if(chosenCompany.getAvailableStocks() > 0)
                        decider.getPlayer().addStock(chosenCompany.getStock());
                }
                
                new java.util.Timer().schedule( 
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            setState(nextState);
                        }
                    }, 0);
                break;
        }
    }
    
    /**
     * Requests the decider to chose which of the follow companies should be merged.
     * @param options Corporations that may be dissolved but are the same size.
     * @param winner Corporation that will dissolve the options but dissolving 
     * the chosen option first.
     * @return Returns the Corporation that the decider wants to win the corporation first.
     */
    public Corporation getFirstDissolved(List<Corporation> options, Corporation winner)
    {
        return decider.choseFirstDissolved(options, winner);
    }
    
    /**
     * Call this once all players have finished their merger actions and then
     * the player will finish his/her turn.
     */
    public void mergerComplete()
    {
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    setState(ComputerState.BUY_STOCKS);
                }
            }, 0);
    }
    @Override
    protected void stateEnded(ComputerState state) 
    {
        
    }
    
    
    
    public static enum ComputerState {PLAY_PIECE, MERGER, BUY_STOCKS};
    /**
     * State map for the turn states.
     */
    public static final HashMap<ComputerState, EnumSet<ComputerState>> stateMap;
    /**
     * setups turn states.
     */
    static {
        stateMap = new HashMap<>();
        stateMap.put(ComputerState.PLAY_PIECE, EnumSet.of(ComputerState.MERGER, 
                ComputerState.BUY_STOCKS));
        stateMap.put(ComputerState.MERGER, EnumSet.of(ComputerState.BUY_STOCKS));
        stateMap.put(ComputerState.BUY_STOCKS, EnumSet.noneOf(ComputerState.class));
    }
}
