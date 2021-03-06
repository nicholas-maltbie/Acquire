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

import com.flyingblock.acquire.model.Investor;
import java.net.Socket;

/**
 * Investor that plays in an inter-net game.
 * @author Nicholas Maltbie
 */
public class NetInvestor 
{
    /**Client thread that holds information and is used to interact with
     * the connected player*/
    private ClientThread client;
    /**Investor that holds data for the game player*/
    private Investor investor;
    /**
     * Constructs a NetInvestor.
     * @param investor Game player.
     * @param client Real player that is connected.
     */
    public NetInvestor(Investor investor, ClientThread client)
    {
        this.investor = investor;
        this.client = client;
    }
    /**
     * Gets the investor for this client.
     * @return Returns the investor that this client plays.
     */
    public Investor getPlayer()
    {
        return investor;
    }
    /**
     * Sends an event to the client.
     * @param event Event to send.
     */
    public void sendMessage(GameEvent event)
    {
        client.sendData(event);
    }
    /**
     * Sends an object to the client.
     * @param object to send.
     */
    public void sendObject(Object event)
    {
        client.sendData(event);
    }
    
    /**
     * Gets the socket of the player connected to the server.
     * @return Returns the client thread's socket.
     */
    public Socket getSocket()
    {
        return client.getSocket();
    }
}
