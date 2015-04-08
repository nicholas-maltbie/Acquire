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
import com.flyingblock.acquire.model.HotelMarket;
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
    private ConcreteServer server;
    /**List of computer players in the game*/
    private List<Decider> computerPlayers;
    /**Human players connected to the network*/
    private List<NetInvestor> humanPlayers;
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
    public AcquireServer(ConcreteServer server, List<Decider> computerPlayers, 
            List<NetInvestor> humanPlayers, AcquireBoard board,
            List<Corporation> companies, HotelMarket market, int delay) {
        super(stateMap, ServerState.GAME_START);
        this.server = server;
        this.computerPlayers = computerPlayers;
        this.humanPlayers = humanPlayers;
        this.board = board;
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
                server.stopAccepting();
                break;
        }
    }

    @Override
    protected void stateEnded(ServerState state)
    {
        
    }
    
    /**
     * Updates the game for every human player.
     */
    private void sendGameUpdate()
    {
        
    }

    @Override
    public void objectRecieved(Socket client, Object message) {
        
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
