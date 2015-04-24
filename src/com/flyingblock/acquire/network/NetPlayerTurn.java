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

import com.flyingblock.acquire.controller.AbstractFSM;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Location;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A finite state machine for a net player's turn.
 * @author Nicholas Maltbie
 */
public class NetPlayerTurn extends AbstractFSM<NetPlayerTurn.TurnState> implements
        ServerListener
{
    /**Server. */
    private AcquireServer server;
    /**Player. */
    private NetInvestor player;
    /**Board. */
    private AcquireBoard board;
    /**Companies. */
    private List<Corporation> companies;
    /**Deck. */
    private HotelMarket deck;

    public NetPlayerTurn(AcquireServer server, NetInvestor player,AcquireBoard board, 
            List<Corporation> companies, HotelMarket deck)
    {
        super(stateMap, TurnState.PLACE_PIECE);
        this.server = server;
        this.player = player;
        this.board = board;
        this.companies = companies;
        this.deck = deck;
    }

    @Override
    protected void stateStarted(TurnState state)
    {
        switch(state)
        {
            case PLACE_PIECE:
                Hotel[] hand = new Hotel[player.getPlayer().getHandSize()];
                for(int i = 0; i < hand.length; i++)
                    hand[i] = player.getPlayer().getFromHand(i);
                GameEvent piecePrompt = EventType.createEvent(EventType.PLAY_PIECE, hand);
                player.sendMessage(piecePrompt);
                break;
            case BUY_STOCKS:
                GameEvent buyPrompt = EventType.createEvent(EventType.BUY_STOCKS, 
                        companies.toArray(new Corporation[companies.size()]));
                player.sendMessage(buyPrompt);
                break;
        }
    }

    @Override
    protected void stateEnded(TurnState state)
    {
        switch(state)
        {
            
        }
    }

    /**holding variable*/
    private Corporation chosenCompany;
    
    @Override
    public void objectRecieved(Socket client, Object message) {
        if(client.equals(player.getSocket()) && message instanceof GameEvent)
        {
            GameEvent event = (GameEvent) message;
            EventType type = EventType.identifyEvent(event);
            switch(type)
            {
                case PIECE_PLAYED:
                    Hotel hotel = (Hotel) event.getMessage();
                    boolean isValid = true;
                    //check if valid
                    
                    if(isValid)
                    {
                        board.set(hotel.getLocation().getRow(), 
                                hotel.getLocation().getCol(), hotel);
                        server.updateAllClients();
                        Location loc = hotel.getLocation();
                        //choose next state
                        List<Corporation> accessories = board.getCorporationsInBlob(
                                loc.getRow(), loc.getCol());

                        final TurnState nextState;

                        //Choose if a merger happens.
                        boolean merger = accessories.size() > 1;
                        List<Corporation> taken = board.getCompaniesOnBoard();
                        List<Corporation> available = new ArrayList<>();
                        for(Corporation c : board.getCompaniesOnBoard())
                            if(!taken.contains(c))
                                available.add(c);
                        //Chose if a new company is formed.
                        List<Location> blob = board.getBlob(loc.getRow(), loc.getCol());
                        boolean formCompany = available.size() > 0 && blob.size() >= 2;
                        for(Location l : blob)
                            if(board.getCorporation(l.getRow(), l.getCol()) != null)
                                formCompany = false;
                        if(merger)
                            nextState = TurnState.MERGER;
                        else
                            nextState = TurnState.BUY_STOCKS;
                        
                        //FIX THIS
                        if(formCompany && !merger)
                        {
                            chosenCompany = null;
                            player.sendMessage(EventType.createEvent(EventType.CREATE_CORPORATION,
                                    available.toArray(new Corporation[available.size()])));
                            while(chosenCompany == null)
                                try {
                                    Thread.sleep(10l);
                                } catch (InterruptedException ex) {
                                    Logger.getLogger(NetPlayerTurn.class.getName()).log(Level.SEVERE, null, ex);
                                }
                            chosenCompany.setHeadquarters(loc);
                            chosenCompany.incorporateRegoin();
                            if(chosenCompany.getAvailableStocks() > 0)
                                player.getPlayer().addStock(chosenCompany.getStock());
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
                    else
                    {
                        //resend request
                        Hotel[] hand = new Hotel[player.getPlayer().getHandSize()];
                        for(int i = 0; i < hand.length; i++)
                            hand[i] = player.getPlayer().getFromHand(i);
                        GameEvent piecePrompt = EventType.createEvent(EventType.PLAY_PIECE, hand);
                        player.sendMessage(piecePrompt);
                    }
                    break;
            }
        }
    }
    
    public void mergerComplete()
    {
        
    }

    @Override
    public void joinedNetwork(Socket client) {
    
    }

    @Override
    public void disconnectedFromNetwork(Socket client) {
    
    }

    @Override
    public void connectionRejected(Socket client) {
    
    }
    
    
    /**Turn states. */
    public static enum TurnState {PLACE_PIECE, MERGER, CREATE_COMPANY, 
            BUY_STOCKS, END_TURN};
    /**State map for the turn states. */
    public static final HashMap<TurnState, EnumSet<TurnState>> stateMap;
    /**setups turn states. */
    static {
        stateMap = new HashMap<>();
        stateMap.put(TurnState.PLACE_PIECE, EnumSet.of(TurnState.MERGER, 
                TurnState.BUY_STOCKS, TurnState.CREATE_COMPANY));
        stateMap.put(TurnState.MERGER, EnumSet.of(TurnState.BUY_STOCKS));
        stateMap.put(TurnState.CREATE_COMPANY, EnumSet.of(TurnState.BUY_STOCKS));
        stateMap.put(TurnState.BUY_STOCKS, EnumSet.of(TurnState.END_TURN));
        stateMap.put(TurnState.END_TURN, EnumSet.noneOf(TurnState.class));
    }
}
