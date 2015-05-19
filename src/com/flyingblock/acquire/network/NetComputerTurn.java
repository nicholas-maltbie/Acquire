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
import com.flyingblock.acquire.model.Stock;
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
        super(stateMap, ComputerState.COMPUTER_PLAY_PIECE);
        this.decider = decider;
        this.acquireServer = machine;
        this.deck = deck;
    }

    @Override
    protected void stateStarted(ComputerState state)
    {
        switch(state)
        {
            case COMPUTER_BUY_STOCKS:
                for(int i = 0; i < decider.getPlayer().getHandSize(); i++)
                    if(decider.getPlayer().getFromHand(i) != null && !AcquireRules.canPieceBePlayed(decider.getPlayer().getFromHand(i), decider.getBoard()))
                        decider.getPlayer().removeFromHand(i);
                List<Stock> before = new ArrayList<>(decider.getPlayer().getStocks());
                decider.buyStocks(decider.getBoard().getCompaniesOnBoard());
                List<Stock> bought = new ArrayList<>(decider.getPlayer().getStocks());
                for(Stock stock : before)
                {
                    bought.remove(stock);
                }
                String stocksMessage = decider.getPlayer().getName() + " bought stocks ";
                for(Stock s : bought)
                {
                    stocksMessage += s.getOwner().getCorporateName() + " ";
                }
                acquireServer.reportEvent(stocksMessage);
                decider.getPlayer().drawFromDeck(deck);
                List<Hotel> hotels = new ArrayList<>();
                for(int i = 0; i < decider.getPlayer().getHandSize(); i++)
                {
                    if(decider.getPlayer().getFromHand(i) != null && 
                            !AcquireRules.canPieceBePlayed(decider.getPlayer().getFromHand(i), decider.getBoard()))
                        hotels.add(decider.getPlayer().getFromHand(i));
                }
                decider.removeNonPlayableTiles(hotels);
                acquireServer.turnEnded(decider.getPlayer());
                break;
            case COMPUTER_MERGER:
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
            case COMPUTER_PLAY_PIECE:
                Investor investor = decider.getPlayer();
                Hotel play = decider.getPlayOption();
                for(int i = 0; i < investor.getHandSize(); i++)
                    if(investor.getFromHand(i) != null && investor.getFromHand(i).equals(play))
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
                //System.out.println(play);
                List<Location> blob = decider.getBoard().getBlob(loc.getRow(), loc.getCol());
                boolean formCompany = available.size() > 0 && blob.size() >= 2;
                for(Location l : blob)
                    if(decider.getBoard().getCorporation(l.getRow(), l.getCol()) != null)
                        formCompany = false;
                if(merger)
                    nextState = ComputerState.COMPUTER_MERGER;
                else
                    nextState = ComputerState.COMPUTER_BUY_STOCKS;
                
                if(formCompany && !merger)
                {
                    Corporation chosenCompany =  decider.createCorporation(available);
                    chosenCompany.setHeadquarters(play.getLocation());
                    chosenCompany.incorporateRegoin();
                    if(chosenCompany.getAvailableStocks() > 0)
                        decider.getPlayer().addStock(chosenCompany.getStock());
                    acquireServer.reportEvent(decider.getPlayer().getName()+ 
                            " established " + chosenCompany.getCorporateName());
                }
                acquireServer.reportEvent(decider.getPlayer().getName() + " played " + play.getLocationText());
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
                    setState(ComputerState.COMPUTER_BUY_STOCKS);
                }
            }, 0);
    }
    @Override
    protected void stateEnded(ComputerState state) 
    {
        
    }
    
    
    
    public static enum ComputerState {COMPUTER_PLAY_PIECE, COMPUTER_MERGER, COMPUTER_BUY_STOCKS};
    /**
     * State map for the turn states.
     */
    public static final HashMap<ComputerState, EnumSet<ComputerState>> stateMap;
    /**
     * setups turn states.
     */
    static {
        stateMap = new HashMap<>();
        stateMap.put(ComputerState.COMPUTER_PLAY_PIECE, EnumSet.of(ComputerState.COMPUTER_MERGER, 
                ComputerState.COMPUTER_BUY_STOCKS));
        stateMap.put(ComputerState.COMPUTER_MERGER, EnumSet.of(ComputerState.COMPUTER_BUY_STOCKS));
        stateMap.put(ComputerState.COMPUTER_BUY_STOCKS, EnumSet.noneOf(ComputerState.class));
    }
}
