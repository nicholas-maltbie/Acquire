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
public class Server extends AbstractServer
{
    /**listeners to this class*/
    private final List<ServerListener> listeners;

    /**
     * Constructs a concrete server on a given port.
     * @param port Port that the clients need to connect to.
     */
    public Server(int port) {
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
    public void removeListener(ServerListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void objectRecieved(Socket client, Object message)
    {
        for(int i = 0; i < listeners.size(); i++)
        {
            ServerListener listener = listeners.get(i);
            listener.objectRecieved(client, message);
        }
    }

    @Override
    public void joinedNetwork(Socket client) 
    {
        for(int i = 0; i < listeners.size(); i++)
        {
            ServerListener listener = listeners.get(i);
            listener.joinedNetwork(client);
        }
    }

    @Override
    public void disconnectedFromNetwork(Socket client) 
    {
        for(int i = 0; i < listeners.size(); i++)
        {
            ServerListener listener = listeners.get(i);
            listener.disconnectedFromNetwork(client);
        }
    }

    @Override
    public void connectionRejected(Socket client) 
    {
        for(int i = 0; i < listeners.size(); i++)
        {
            ServerListener listener = listeners.get(i);
            listener.connectionRejected(client);
        }
    }
    
}
