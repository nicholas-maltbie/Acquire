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
import com.flyingblock.acquire.controller.InvestorComparator;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.network.AcquireServer.ServerState;
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
    private NetComputerTurn computerTurn;
    private NetworkMerger merger;

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
        currentPlayer = 0;// (int)(Math.random() * players.size());
        server.addListener(this);
    }

    @Override
    protected void stateStarted(ServerState state)
    {
        switch(state)
        {
            case GAME_START:
                //System.out.println("STARTING");
                for(Investor i : gamePlayers)
                {
                    i.drawFromDeck(market);
                    for(int j = 0; j < 1; j++)
                    {
                        Hotel h = market.draw();
                        board.set(h.getLocation().getRow(), h.getLocation().getCol(), h);
                    }
                }
                server.stopAccepting();
                updateAllClients();
                setState(ServerState.PLAYER_TURN);
                break;
            case PLAYER_TURN:
                //System.out.println(getCurrentPlayer().getName());
                if(isPlayerComputer(getCurrentPlayer()))
                {
                    //do computer turn
                     computerTurn = new NetComputerTurn(
                             getDeciderFor(getCurrentPlayer()), this, market);
                     computerTurn.start();
                }
                else
                {
                    //do Human turn, ugh! humans are so slow!
                    humanTurn = new NetPlayerTurn(this, getClientFor(getCurrentPlayer()), 
                        board, companies, market);
                    server.addListener(humanTurn);
                    humanTurn.start();
                }
                
                break;
            case GAME_END:
                //Hand out majority and minority bonuses
                for(Corporation c : companies)
                    handOutMajorityAndMinorityBonuses(c);
                //cash in player stocks
                for(Investor p : gamePlayers)
                    while(p.getNumStocks() > 0)
                        p.addMoney(p.removeStock(0).getTradeInValue());
                //organize players by money.
                List<Investor> allPlayers = new ArrayList<>(gamePlayers);
                allPlayers.sort(new InvestorComparator());
                updateAllClients();
                reportEvent(allPlayers.get(0).getName() + 
                        " won the game with $" + allPlayers.get(0).getMoney());
                for(NetInvestor player : humanPlayers)
                {
                    player.sendMessage(EventType.createEvent(EventType.GAME_END, 
                            allPlayers.toArray(new Investor[allPlayers.size()])));
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
        AcquireBoard boardCopy = board.copy();
        Investor[] players = new Investor[gamePlayers.size()];
        for(int i = 0; i < gamePlayers.size(); i++)
            players[i] = new Investor(gamePlayers.get(i));
        Corporation[] corps = new Corporation[companies.size()];
        for(int i = 0; i < companies.size(); i++)
            corps[i] = new Corporation(companies.get(i));
        for(NetInvestor netPlayer : humanPlayers)
        {
            ClientThread client = server.getClient(netPlayer.getSocket());
            client.sendData(EventType.createEvent(EventType.BOARD_UPDATE, boardCopy));
            client.sendData(EventType.createEvent(EventType.PLAYERS_UPDATE, players));
            client.sendData(EventType.createEvent(EventType.CORPORATIONS_UPDATE, corps));
        }
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
    public void sendGameUpdate(ClientThread client)
    {
    }

    @Override
    public void objectRecieved(Socket client, Object message)
    {
        
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        
    }

    @Override
    public void disconnectedFromNetwork(Socket client)
    {
        int i = 0; 
        while(i < humanPlayers.size())
        {
            if(humanPlayers.get(i).getSocket().equals(client))
                humanPlayers.remove(i);
            else
                i++;
        }
    }

    @Override
    public void connectionRejected(Socket client) 
    {
    
    }
    
    /**Saves a corporation for multi-thread communication. */
    //private Corporation mergerWinner;
    /**Holding variable to wait for a player to send his or her merger requests*/
    //private int[] mergerAction;
    /**
     * Chooses the next company to be merged when they are of equal size.
     * @param options Companies of equal size to choose from.
     * @param winner Company that will win the merger.
     * @param decider player who chooses the winner.
     * @return Returns the chosen corporation.
     */
    /*public Corporation getFirstMerged(List<Corporation> options, Corporation 
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
    }*/
    
    /**
     * Does a merger including, handing out majority and minority bonuses, 
     * prompts human and AI players to make decisions about their stocks, and 
     * dissolves the consumed corporations.
     * @param parent Company that comes out on top.
     * @param food Company(ies) that are consumed by the parent.
     */
    public void handelMerger(Corporation parent, List<Corporation> food)
    {
        merger = new NetworkMerger(this, parent, food);
        server.addListener(merger);
        merger.start();
    }
    
    public void mergerComplete(Corporation parent)
    {
        server.removeListener(merger);
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
    /*public int[] getPlayerMerger(Corporation parent, Corporation child, Investor shareHolder)
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
        while(mergerAction == null)
            try {
                Thread.sleep(10l);
            } catch (InterruptedException ex) {
                Logger.getLogger(AcquireServer.class.getName()).log(Level.SEVERE, null, ex);
            }
        return mergerAction;
    }*/
    
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
    
    public void reportEvent(String event)
    {
        for(NetInvestor player : humanPlayers)
        {
            server.getClient(player.getSocket()).sendData("[GameEvent] " + event);
        }
    }
    
    /**
     * Method called when a player finished his/her/its turn.
     * @param player Player that finished his/her/its turn.
     */
    public void turnEnded(Investor player)
    {
        updateAllClients();
        
        try {
            Thread.sleep(100l);
        } catch (InterruptedException ex) {
            Logger.getLogger(AcquireServer.class.getName()).log(Level.SEVERE, null, ex);
        }
        //server.removeListener(humanTurn);
        //System.out.println(board);
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
        
        if(!isPlayerComputer(getCurrentPlayer()))
        {
            server.removeListener(humanTurn);
        }
        
        if(!over)
        {
            //if not, go to next turn
            currentPlayer++;
            currentPlayer %= gamePlayers.size();
            reportEvent("It is now " + getCurrentPlayer().getName() + "'s turn");
            setState(ServerState.PLAYER_TURN);
        }
        else
        {
            setState(ServerState.GAME_END);
        }
    }
    
    public int getCurrentPlayerIndex()
    {
        return currentPlayer;
    }
    
    public int getNumPlayers()
    {
        return gamePlayers.size();
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
