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
import com.flyingblock.acquire.network.AcquireServer.ServerState;
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
{
    private ConcreteServer server;
    private List<Decider> computerPlayers;
    private List<NetInvestor> humanPlayers;

    public AcquireServer() {
        super(stateMap, ServerState.GAME_START);
    }

    @Override
    protected void stateStarted(ServerState state) {
        
    }

    @Override
    protected void stateEnded(ServerState state) {
        
    }
    
    public static enum ServerState {GAME_START, HUMAN_TURN, COMPUTER_TURN};
    public final static Map<ServerState, EnumSet<ServerState>> stateMap;
    
    static {
        stateMap = new HashMap<>();
        stateMap.put(ServerState.GAME_START, EnumSet.of(ServerState.HUMAN_TURN,
                ServerState.COMPUTER_TURN));
    }
}
