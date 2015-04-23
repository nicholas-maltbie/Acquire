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

import com.flyingblock.acquire.computer.Decider;
import com.flyingblock.acquire.controller.AbstractFSM;
import com.flyingblock.acquire.controller.AcquireRules;
import com.flyingblock.acquire.controller.MergerPanel;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.network.AcquireServer.ServerState;
import java.awt.Color;
import java.awt.Rectangle;
import java.net.Socket;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * A server that will host an Acquire game and will run a game with given clients
 * that are already connected to a provided server.
 * @author Nicholas Maltbie
 */
public class AcquireServer extends AbstractFSM<AcquireServer.ServerState> 
    implements ServerListener
{
    /**Server to communicate with clients*/
    private Server server;
    /**List of computer players in the game*/
    private List<Decider> computerPlayers;
    /**Human players connected to the network*/
    private List<NetInvestor> humanPlayers;
    /**Players that are controlled by a NetInvestor or Decider*/
    private List<Investor> gamePlayers;
    /**Current player that is taking his/her/its turn*/
    private int currentPlayer;
    /**Game board*/
    private AcquireBoard board;
    /**Companies*/
    private List<Corporation> companies;
    /**Draw pile for hotels*/
    private HotelMarket market;
    /**Delay in milliseconds between turns*/
    private int delay;
    /**Current human player turn if the player is a human*/
    private NetPlayerTurn humanTurn;
    /**Current computer player turn if the player is a computer*/
    private ComputerNetTurn computerTurn;

    /**
     * Constructs an acquire server.
     * @param server Server that the human players are connected to. Players
     * must already have been connected to the server.
     * @param computerPlayers Computer players that make AI decisions.
     * @param humanPlayers Human players that are playing the game.
     * @param players Game Players in turn order.
     * @param board Game board.
     * @param companies Companies in the game.
     * @param market Deck to draw cards from.
     * @param delay Delay between player and AI turns, it allows humans to see
     * the changes and keep up with the game.
     */
    public AcquireServer(Server server, List<Decider> computerPlayers, 
            List<NetInvestor> humanPlayers, List<Investor> players, AcquireBoard board,
            List<Corporation> companies, HotelMarket market, int delay) {
        super(stateMap, ServerState.GAME_START);
        this.server = server;
        this.computerPlayers = computerPlayers;
        this.humanPlayers = humanPlayers;
        this.board = board;
        this.gamePlayers = players;
        this.companies = companies;
        this.market = market;
        this.delay = delay;
        currentPlayer = (int)(Math.random() * players.size());
        server.addListener(this);
    }

    @Override
    protected void stateStarted(ServerState state)
    {
        switch(state)
        {
            case GAME_START:
                for(Investor i : gamePlayers)
                {
                    i.drawFromDeck(market);
                    Hotel h = market.draw();
                    board.set(h.getLocation().getRow(), h.getLocation().getCol(), h);
                }
                server.stopAccepting();
                for(NetInvestor netPlayer : humanPlayers)
                {
                    sendGameUpdate(netPlayer);
                }
                setState(ServerState.PLAYER_TURN);
                break;
            case PLAYER_TURN:
                if(isPlayerComputer(getCurrentPlayer()))
                {
                    //do computer turn
                     computerTurn = new ComputerNetTurn(
                             getDeciderFor(getCurrentPlayer()), this, market);
                     computerTurn.start();
                }
                else
                {
                    //do Human turn, ughhhhh humans are so slow!
                    humanTurn = new NetPlayerTurn(this, getClientFor(getCurrentPlayer()), 
                        board, companies, market);
                    server.addListener(humanTurn);
                    humanTurn.start();
                }
                
                break;
        }
    }

    @Override
    protected void stateEnded(ServerState state)
    {
        switch(state)
        {
            case PLAYER_TURN:
                updateAllClients();
                break;
        }
    }
    
    /**
     * Updates all the clients' data of the game.
     */
    public void updateAllClients()
    {
        for(NetInvestor netPlayer : humanPlayers)
            sendGameUpdate(netPlayer);
    }
    
    /**
     * Gets the current Player.
     * @return Returns the current player's investor.
     */
    public Investor getCurrentPlayer()
    {
        return gamePlayers.get(currentPlayer);
    }
    
    /**
     * Gets a player from the list of investors.
     * @param index Index of player to check.
     * @return Returns the player at that index.
     */
    public Investor getPlayer(int index)
    {
        return gamePlayers.get(index);
    }
    
    /**
     * Checks if there is a Decider for a given investor.
     * @param investor Investor to check.
     * @return Returns true if there is a decider and false if there isn't a
     *  decider.
     */
    public boolean isPlayerComputer(Investor investor)
    {
        boolean computer = false;
        for(Decider d : computerPlayers)
        {
            if(d.getPlayer().equals(investor))
                computer = true;
        }
        return computer;
    }
    
    /**
     * Gets the decider for a given investor if the investor is a computer player.
     * @param investor Computer player to get the decider for.
     * @return Returns the decider for the computer investor if there is a decider
     * for the computer investor. If there is no decider or this is not a computer player,
     * it will return null because there is no decider to get.
     */
    public Decider getDeciderFor(Investor investor)
    {
        if(isPlayerComputer(investor))
            for(Decider d : computerPlayers)
                if(d.getPlayer().equals(investor))
                    return d;
        return null;
    }
    /**
     * Gets the NetInvestor for a given investor if the investor is a human player.
     * @param investor Human player to get the client for.
     * @return Returns the NetInvestor for the human investor if there is a NetInvestor
     * for the human investor. If there is no NetInvestor or this is not a human player,
     * it will return null because there is no NetInvestor to get.
     */
    public NetInvestor getClientFor(Investor investor)
    {
        if(!isPlayerComputer(investor))
            for(NetInvestor client : humanPlayers)
                if(client.getPlayer().equals(investor))
                    return client;
        return null;
    }
    
    /**
     * Sends game updates to a client
     * @param client Client to send Update to.
     */
    public void sendGameUpdate(NetInvestor client)
    {
        client.sendMessage(EventType.createEvent(EventType.BOARD_UPDATE, board));
        client.sendMessage(EventType.createEvent(EventType.PLAYERS_UPDATE, 
                gamePlayers.toArray(new Investor[gamePlayers.size()])));
        client.sendMessage(EventType.createEvent(EventType.CORPORATIONS_UPDATE,
                companies.toArray(new Corporation[companies.size()])));
    }

    @Override
    public void objectRecieved(Socket client, Object message)
    {
        if(clientOfInterest != null && clientOfInterest.equals(client))
        {
            if(message instanceof GameEvent)
            {
                GameEvent event = (GameEvent) message;
                switch(EventType.identifyEvent(event))
                {
                    case MERGED_FIRST:
                        mergerWinner = (Corporation) event.getMessage();
                        break;
                    case MERGER_ACTION:
                        mergerAction = (int[]) event.getMessage();
                        break;
                }
            }
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
    
    /**Saves a corporation for multi-thread communication. */
    private Corporation mergerWinner;
    /**Holding variable to wait for a player to send his or her merger requests*/
    private int[] mergerAction;
    /**Client that the server wants to get a message from*/
    private Socket clientOfInterest;
    /**
     * Chooses the next company to be merged when they are of equal size.
     * @param options Companies of equal size to choose from.
     * @param winner Company that will win the merger.
     * @param decider player who chooses the winner.
     * @return Returns the chosen corporation.
     */
    public Corporation getFirstMerged(List<Corporation> options, Corporation 
            winner, Investor decider)
    {
        //computer player
        if(isPlayerComputer(decider))
        {
            return getDeciderFor(decider).choseFirstDissolved(options, winner);
        }
        //human player
        else
        {
            mergerWinner = null;
            clientOfInterest = getClientFor(decider).getSocket();
            getClientFor(decider).sendMessage(EventType.createEvent(EventType.CHOOSE_FIRST,
                    options.toArray(new Corporation[options.size()])));
            while(mergerWinner == null)
                try {
                    Thread.sleep(10l);
                } catch (InterruptedException ex) {
                    Logger.getLogger(AcquireServer.class.getName()).log(Level.SEVERE, null, ex);
                }
            return mergerWinner;
        }
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
                index = index % this.gamePlayers.size();
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
        }
        parent.incorporateRegoin();
        this.updateAllClients();
        //tell the turn that the players have completed their actions.
        if(isPlayerComputer(getCurrentPlayer()))
        {
            computerTurn.mergerComplete();
        } 
        else
        {
            humanTurn.mergerComplete();
        }
    }
    
    /**
     * Gets a player merger action where he or she will have to hold, trade in,
     * or sell all of his or her stocks in the child company. If this is a client,
     * it will hold the current thread until the action is completed
     * @param parent Company that eats the child.
     * @param child Company being merged.
     * @param shareHolder Investor taking a merger action.
     * @return Returns the actions that the player took in the format:
     *  {sold, traded}.
     */
    public int[] getPlayerMerger(Corporation parent, Corporation child, Investor shareHolder)
    {
        //AI player
        if(isPlayerComputer(shareHolder))
        {
            if(getDeciderFor(shareHolder) != null)
            {
                return getDeciderFor(shareHolder).getMergerActions(parent, child);
            }
        }
        //Human player
        mergerAction = null;
        getClientFor(shareHolder).sendMessage(EventType.createEvent(
                EventType.TAKE_MERGER, new Corporation[]{parent, child}));
        clientOfInterest = getClientFor(shareHolder).getSocket();
        while(mergerAction == null)
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                Logger.getLogger(AcquireServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        return mergerAction;
    }
    
    /**
     * Gives out the majority and minority bonuses to players in the game.
     * @param c Company to hand out majority and minority bonuses.
     */
    public void handOutMajorityAndMinorityBonuses(Corporation c)
    {
        List<Investor> majority = new ArrayList<>();
        for(Investor investor : gamePlayers)
        {
            if(investor.getStocks(c) > 0)
            {
                if(majority.isEmpty())
                    majority.add(investor);
                else if(investor.getStocks(c) > majority.get(0).getStocks(c))
                {
                    majority.clear();
                    majority.add(investor);
                }
                else if(investor.getStocks(c) == majority.get(0).getStocks(c))
                    majority.add(investor);
            }
        }

        List<Investor> minority = new ArrayList<>();
        for(Investor investor : gamePlayers)
        {
            if(investor.getStocks(c) > 0 && !majority.contains(investor))
            {
                if(minority.isEmpty())
                    minority.add(investor);
                else if(investor.getStocks(c) > minority.get(0).getStocks(c))
                {
                    minority.clear();
                    minority.add(investor);
                }
                else if(investor.getStocks(c) == minority.get(0).getStocks(c))
                    minority.add(investor);
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
     * Method called when a player finished his/her/its turn.
     * @param player Player that finished his/her/its turn.
     */
    public void turnEnded(Investor player)
    {
        server.removeListener(humanTurn);
        //Decide if game is over
        List<Corporation> established = board.getCompaniesOnBoard();
        boolean over = false;
        if(!established.isEmpty())
        {
            boolean full = false;
            boolean allSafe = true;
            for(Corporation c : established)
            {
                if(c.getNumberOfHotels() >= AcquireRules.END_CORPORATION_SIZE)
                    full = true;
                if(c.getNumberOfHotels() < AcquireRules.SAFE_CORPORATION_SIZE)
                    allSafe = false;
            }
            over = full || allSafe;
        }
        
        if(!over)
        {
            //if not, go to next turn
            currentPlayer++;
            currentPlayer %= gamePlayers.size();
            setState(ServerState.PLAYER_TURN);
        }
        else
        {
            setState(ServerState.GAME_END);
        }
    }
    
    public static enum ServerState {GAME_START, PLAYER_TURN,
            GAME_END};
    public final static Map<ServerState, EnumSet<ServerState>> stateMap;
    
    static {
        stateMap = new HashMap<>();
        stateMap.put(ServerState.GAME_START, EnumSet.of(ServerState.PLAYER_TURN));
        stateMap.put(ServerState.PLAYER_TURN, EnumSet.of(ServerState.GAME_END, ServerState.PLAYER_TURN));
        stateMap.put(ServerState.GAME_END, EnumSet.of(ServerState.GAME_START));
    }
}
