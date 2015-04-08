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
import java.util.ArrayList;
import java.util.List;

/**
 * A server that will receive events from a client then tell its listeners about
 * the events that occur.
 * @author Nicholas Maltbie
 */
public class ConcreteServer extends Server
{
    /**listeners to this class*/
    private final List<ServerListener> listeners;

    /**
     * Constructs a concrete server on a given port.
     * @param port Port that the clients need to connect to.
     */
    public ConcreteServer(int port) {
        super(port);
        listeners = new ArrayList<>();
    }
    
    /**
     * Allows a listener to listen to events of the server.
     * @param listener Listener to add.
     */
    public void addListener(ServerListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Removes a listener from the listeners listening to events.
     * @param listener Listener to remove
     */
    public void removeLIstener(ServerListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void objectRecieved(Socket client, Object message)
    {
        synchronized(listeners)
        {
            for(ServerListener listener : listeners)
                listener.objectRecieved(client, message);
        }
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        synchronized(listeners)
        {
            for(ServerListener listener : listeners)
                listener.joinedNetwork(client);
        }
    }

    @Override
    public void disconnectedFromNetwork(Socket client) 
    {
        synchronized(listeners)
        {
            for(ServerListener listener : listeners)
                listener.disconnectedFromNetwork(client);
        }
    }

    @Override
    public void connectionRejected(Socket client) 
    {
        synchronized(listeners)
        {
            for(ServerListener listener : listeners)
                listener.connectionRejected(client);
        }
    }
    
}
