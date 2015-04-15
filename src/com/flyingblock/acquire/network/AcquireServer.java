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
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.network.AcquireServer.ServerState;
import java.net.Socket;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     * Constructs an acquire server.
     * @param server Server that the human players are connected to. Players
     * must already have been connected to the server.
     * @param computerPlayers Computer players that make AI decisions.
     * @param humanPlayers Human players that are playing the game.
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
                break;
        }
    }

    @Override
    protected void stateEnded(ServerState state)
    {
        
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
    
    public static enum ServerState {GAME_START, HUMAN_TURN, COMPUTER_TURN,
            END_TURN, GAME_END};
    public final static Map<ServerState, EnumSet<ServerState>> stateMap;
    
    static {
        stateMap = new HashMap<>();
        stateMap.put(ServerState.GAME_START, EnumSet.of(ServerState.HUMAN_TURN,
                ServerState.COMPUTER_TURN));
        stateMap.put(ServerState.HUMAN_TURN, EnumSet.of(ServerState.END_TURN));
        stateMap.put(ServerState.COMPUTER_TURN, EnumSet.of(ServerState.END_TURN));
        stateMap.put(ServerState.END_TURN, EnumSet.of(ServerState.HUMAN_TURN,
                ServerState.COMPUTER_TURN, ServerState.GAME_END));
        stateMap.put(ServerState.GAME_END, EnumSet.of(ServerState.GAME_START));
    }
}
