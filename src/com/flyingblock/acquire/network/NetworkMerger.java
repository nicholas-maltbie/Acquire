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
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Investor;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Maltbie_N
 */
public class NetworkMerger extends AbstractFSM<NetworkMerger.MergerState> implements
        ServerListener
{
    private AcquireServer server;
    private Corporation parent;
    private List<Corporation> food;
    private Corporation next;
    private NetInvestor currentPlayer;
    private List<Investor> shareHolders;
    private int playerIndex;
    
    public NetworkMerger(AcquireServer server, Corporation parent, List<Corporation> food) {
        super(stateMap, MergerState.START);
        this.server = server;
        this.parent = parent;
        this.food = food;
    }

    @Override
    protected void stateStarted(MergerState state) 
    {
        switch(state)
        {
            case START:
                int newSize = parent.getNumberOfHotels() + 1;
                for(Corporation c : food)
                    newSize += c.getNumberOfHotels();
                parent.incorporateRegoin();
                //Hand out majority and minory bonus for all food eaten.
                for(Corporation c : food)
                {
                    server.handOutMajorityAndMinorityBonuses(c);
                }
                setState(MergerState.SELECT_NEXT);
                break;
            case SELECT_NEXT:
                if(next != null)
                {
                    food.remove(next);
                    next.dissolve();
                    parent.incorporateRegoin();
                    server.updateAllClients();
                }
                if(!food.isEmpty())
                {
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

                    //get the next corporaiton to be dissolved
                    if(largest.size() > 1)
                        promptFirstMerged(largest, parent, server.getCurrentPlayer());
                    else
                    {
                        next = largest.get(0);
                        this.setState(MergerState.GET_DECISION);
                    }
                }
                else
                {
                    this.setState(MergerState.END_MERGER);
                }
                break;
            case GET_DECISION:
                if(!shareHolders.isEmpty())
                {
                    Investor shareHolder = shareHolders.get(playerIndex);
                    if(server.isPlayerComputer(shareHolder))
                    {
                        //get the action of the current player. This should puase the thread
                        // and stop other players from taking actions
                        int[] action = server.getDeciderFor(shareHolder).getMergerActions(parent, next);
                        doPlayerMerger(action, shareHolder);
                    }
                    else
                    {
                        server.getClientFor(shareHolder).sendMessage(EventType.createEvent(
                                EventType.TAKE_MERGER, new Corporation[]{parent, next}));
                    }
                }
                else
                {
                    this.setState(MergerState.SELECT_NEXT);
                }
                break;
            case END_MERGER:
                //finalize the action
                next.dissolve();
                parent.incorporateRegoin();
                server.updateAllClients();
                server.mergerComplete(parent);
                break;
        }
    }
    
    public void doPlayerMerger(int[] action, Investor shareHolder)
    {
        //do the action
        int traded = action[1];
        for(int i = 0; i < traded/2; i++)
        {
            next.returnStock(shareHolder.removeStock(next));
            next.returnStock(shareHolder.removeStock(next));
            shareHolder.addStock(parent.getStock());
        }
        int sold = action[0];
        for(int i = 0; i < sold; i++)
        {
            next.returnStock(shareHolder.removeStock(next));
            shareHolder.addMoney(next.getStockPrice());
        }
        //tell clients what's up
        server.updateAllClients();
        playerIndex++;
        if(playerIndex < shareHolders.size())
            this.setState(MergerState.GET_DECISION);
        else
            this.setState(MergerState.SELECT_NEXT);
    }
    
    public void promptFirstMerged(List<Corporation> options, Corporation winner, Investor player)
    {
        if(server.isPlayerComputer(player))
        {
            next = server.getDeciderFor(player).choseFirstDissolved(options, winner);
            this.setState(MergerState.GET_DECISION);
        }
        else
        {
            NetInvestor decider = server.getClientFor(player);
            currentPlayer = decider;
            decider.sendMessage(EventType.createEvent(EventType.CHOOSE_FIRST,
                    options.toArray(new Corporation[options.size()])));
        }
    }
    
    @Override
    public void objectRecieved(Socket client, Object message) 
    {
            GameEvent event = (GameEvent) message;
            EventType type = EventType.identifyEvent(event);
            switch(type)
            {
                case MERGED_FIRST:
                    if(client.equals(currentPlayer.getSocket()) && message instanceof GameEvent)
                    {
                        next = (Corporation) event.getMessage();
                        this.setState(MergerState.GET_DECISION);
                    }
                    break;
                case MERGER_ACTION:
                    int[] action = (int[]) event.getMessage();
                    doPlayerMerger(action, shareHolders.get(playerIndex));
                    break;
            }
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        
    }

    @Override
    public void disconnectedFromNetwork(Socket client) 
    {
        
    }

    @Override
    public void connectionRejected(Socket client) 
    {
        
    }

    @Override
    protected void stateEnded(MergerState state)
    {
        switch(state)
        {
            case SELECT_NEXT:
                //Players chose what to do with their stocks from the food.
                shareHolders = new ArrayList<>();
                int checked = 0;
                int index = server.getCurrentPlayerIndex();
                playerIndex = 0;
                //get relevant investors.
                while(checked < server.getNumPlayers())
                {
                    if(server.getPlayer(index).getStocks(next) > 0)
                        shareHolders.add(server.getPlayer(index));
                    index = (index+1) % server.getNumPlayers();
                    checked++;
                }
                break;
        }
    }
    /*int newSize = parent.getNumberOfHotels() + 1;
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
            //get the next corporaiton to be dissolved
            if(largest.size() > 1)
                next = getFirstMerged(largest, parent, getCurrentPlayer());
            else
                next = largest.get(0);
            //remove the corporation from the list because it can only be eaten once
            food.remove(next);
            
            //Players chose what to do with their stocks from the food.
            List<Investor> shareHolders = new ArrayList<>();
            int checked = 0;
            int index = currentPlayer;
            //get relevant investors.
            while(checked < gamePlayers.size())
            {
                if(getPlayer(index).getStocks(next) > 0)
                    shareHolders.add(getPlayer(index));
                index = (index+1) % this.gamePlayers.size();
                checked++;
            }
            //have relevant investors take actions in the correct order
            for(Investor shareHolder :shareHolders)
            {
                //get the action of the current player. This should puase the thread
                // and stop other players from taking actions
                int[] action = getPlayerMerger(parent, next, shareHolder);
                //do the action
                
                int traded = action[1];
                for(int i = 0; i < traded/2; i++)
                {
                    next.returnStock(shareHolder.removeStock(next));
                    next.returnStock(shareHolder.removeStock(next));
                    shareHolder.addStock(parent.getStock());
                }
                int sold = action[0];
                for(int i = 0; i < sold; i++)
                {
                    next.returnStock(shareHolder.removeStock(next));
                    shareHolder.addMoney(next.getStockPrice());
                }
                //tell clients what's up
                this.updateAllClients();
            }
            //finalize the action
            next.dissolve();
            parent.incorporateRegoin();
            this.updateAllClients();
        }*/

    
    public static enum MergerState {START, SELECT_NEXT, GET_DECISION, END_MERGER};
    public final static Map<MergerState, EnumSet<MergerState>> stateMap;
    
    static {
        stateMap = new HashMap<>();
        stateMap.put(MergerState.START, EnumSet.of(MergerState.SELECT_NEXT));
        stateMap.put(MergerState.SELECT_NEXT, EnumSet.of(MergerState.GET_DECISION,
                MergerState.END_MERGER));
        stateMap.put(MergerState.GET_DECISION, EnumSet.of(MergerState.GET_DECISION,
                MergerState.SELECT_NEXT));
        stateMap.put(MergerState.END_MERGER, EnumSet.noneOf(MergerState.class));
    }
}
