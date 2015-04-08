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

import java.net.Socket;

/**
 * Class that listens to a server's events.
 * @author Nicholas Maltbie
 */
public interface ServerListener
{
    /**
     * Method called whenever a client sends a object to this server.
     * @param client Client that send the message.
     * @param message Message sent.
     */
    abstract public void objectRecieved(Socket client, Object message);
    /**
     * Method called whenever a client joins the network. This happens before
     * the client is added to the list of clients.
     * @param client Client that joined.
     */
    abstract public void joinedNetwork(Socket client);
    /**
     * Method called whenever a client leaves the network. This happens after
     * the client leaves the network.
     * @param client Client that leaves.
     */
    abstract public void disconnectedFromNetwork(Socket client);
    /**
     * Method called whenever a client is rejected from the network.
     * @param client Client rejected connection
     */
    abstract public void connectionRejected(Socket client);
}
