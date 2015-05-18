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
import com.flyingblock.acquire.controller.AcquireRules;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.Stock;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
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
    
    private List<Corporation> suspects;

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
            case CREATE_COMPANY:
                List<Corporation> taken = board.getCompaniesOnBoard();
                List<Corporation> available = new ArrayList<>();
                for(Corporation c : companies)
                    if(!taken.contains(c))
                        available.add(c);
                player.sendMessage(EventType.createEvent(EventType.CREATE_CORPORATION,
                        available.toArray(new Corporation[available.size()])));
                break;
            case MERGER:
                List<Corporation> suspects = new ArrayList<>();
                for(Location l : board.getBlob(locOfInterest.getRow(), locOfInterest.getCol()))
                    if(board.isIncorporated(l.getRow(), l.getCol()) && 
                            !suspects.contains(board.getCorporation(l.getRow(), l.getCol())))
                        suspects.add(board.getCorporation(l.getRow(), l.getCol()));
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
                    this.suspects = suspects;
                    player.sendMessage(EventType.createEvent(EventType.CHOOSE_WINNER, 
                            largest.toArray(new Corporation[largest.size()])));
                }
                else
                {
                    suspects.remove(parent);
                    server.handelMerger(parent, suspects);
                }
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
    private Location locOfInterest;
    private boolean boughtStocks;
    private boolean createdCorporation;
    
    @Override
    public void objectRecieved(Socket client, Object message) {
        if(client.equals(player.getSocket()) && message instanceof GameEvent)
        {
            GameEvent event = (GameEvent) message;
            EventType type = EventType.identifyEvent(event);
            /*System.out.println(type);
            if(event.getMessage() instanceof Object[])
                System.out.println(Arrays.toString((Object[]) event.getMessage()));
            else
                System.out.println(event.getMessage());*/
            switch(type)
            {
                case TILES_REMOVED:
                    Hotel[] removed = (Hotel[]) event.getMessage();
                    for(int i = 0; i < removed.length; i++)
                    {
                        for(int h = 0; h < player.getPlayer().getHandSize(); h++)
                            if(player.getPlayer().getFromHand(h) != null &&
                                    player.getPlayer().getFromHand(h).getLocation().equals(removed[i].getLocation()))
                                player.getPlayer().removeFromHand(h);
                    }
                    player.getPlayer().drawFromDeck(deck);
                    
                    server.turnEnded(player.getPlayer());
                    break;
                case MERGER_WINNER:
                    if(this.getState() == TurnState.MERGER)
                    {
                        Corporation parent = (Corporation) event.getMessage();
                        suspects.remove(parent);
                        server.handelMerger(parent, suspects);
                    }
                    break;
                case STOCKS_BOUGHT:
                    if(this.getState() == TurnState.BUY_STOCKS && !boughtStocks)
                    {
                        boughtStocks = true;
                        Stock[] bought = (Stock[]) event.getMessage();
                        String stocksMessage = player.getPlayer().getName() + " bought stocks ";
                        for(Stock stock : bought)
                        {
                            if(stock != null && stock.getOwner() != null)
                            {
                                Corporation owner = null;
                                for(Corporation c : companies)
                                {
                                    if(c.getCorporateName().equals(stock.getOwner().getCorporateName()))
                                    {
                                        owner = c;
                                    }
                                }
                                if(owner != null)
                                {
                                    Stock taken = owner.getStock();
                                    player.getPlayer().addStock(taken);
                                    player.getPlayer().addMoney(-stock.getOwner().getStockPrice());
                                    stocksMessage += owner.getCorporateName() + " ";
                                }
                                
                            }
                        }
                        List<Hotel> hotels = new ArrayList<>();
                        for(int i = 0; i < player.getPlayer().getHandSize(); i++)
                        {
                            if(player.getPlayer().getFromHand(i) != null && 
                                    !AcquireRules.canPieceBePlayed(player.getPlayer().getFromHand(i), board))
                                hotels.add(player.getPlayer().getFromHand(i));
                        }
                        server.reportEvent(stocksMessage);
                        player.sendMessage(EventType.createEvent(EventType.REMOVE_TILES,
                                hotels.toArray(new Hotel[hotels.size()])));
                    }
                    break;
                case CORPORATION_CREATED:
                    if(this.getState() == TurnState.CREATE_COMPANY && !createdCorporation)
                    {
                        createdCorporation = true;
                        Corporation created = (Corporation) event.getMessage();
                        boolean validCorporation = true;
                        //check stuff
                        if(validCorporation)
                        {
                            for(int i = 0; i < companies.size(); i++)
                            {
                                Corporation temp = companies.get(i);
                                if(created.getCorporateName().equals(temp.getCorporateName()))
                                {
                                    temp.setHeadquarters(locOfInterest);
                                    temp.incorporateRegoin();
                                    if(temp.getAvailableStocks() > 0)
                                        player.getPlayer().addStock(temp.getStock());
                                    server.updateAllClients();
                                    server.reportEvent(player.getPlayer().getName()+ " established " + created.getCorporateName());
                                    setState(TurnState.BUY_STOCKS);
                                    return;
                                }
                            }
                        }
                    }
                    break;
                case PIECE_PLAYED:
                    if(this.getState() == TurnState.PLACE_PIECE)
                    {
                        Hotel hotel = (Hotel) event.getMessage();
                        boolean validPlay = true;
                        //check if valid
                        if(validPlay)
                        {
                            //System.out.println("PIECE WAS PLAYED");
                            for(int i = 0; i < player.getPlayer().getHandSize(); i++)
                            {
                                if(player.getPlayer().getFromHand(i) != null && 
                                        player.getPlayer().getFromHand(i).equals(hotel))
                                {
                                    player.getPlayer().removeFromHand(i);
                                }
                            }
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
                            for(Corporation c : companies)
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
                            else if(formCompany)
                                nextState = TurnState.CREATE_COMPANY;
                            else
                                nextState = TurnState.BUY_STOCKS;
                            server.reportEvent(player.getPlayer().getName()+ " played " + hotel.getLocationText());
                            locOfInterest = hotel.getLocation();
                            if(this.getState() == TurnState.PLACE_PIECE)
                                setState(nextState);
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
                    }
                    break;
            }
        }
    }
    
    public void mergerComplete()
    {
        
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    setState(TurnState.BUY_STOCKS);
                }
            }, 0);
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
