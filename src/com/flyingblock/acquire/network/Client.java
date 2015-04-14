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
 *
 * @author Maltbie_N
 */
public class Client extends AbstractClient {
    /**listeners to this class*/
    private final List<ClientListener> listeners;

    /**
     * Constructs a concrete server on a given port.
     * @param port Port that the clients need to connect to.
     * @param address Address to connect to.
     */
    public Client(String address, int port) {
        super(address, port);
        listeners = new ArrayList<>();
    }
    
    /**
     * Allows a listener to listen to events of the client.
     * @param listener Listener to add.
     */
    public void addListener(ClientListener listener)
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
    public void objectRecieved(Object object) {
        synchronized(listeners)
        {
            for(ClientListener listener : listeners)
                listener.objectRecieved(object);
        }
    }

    @Override
    public void disconnectedFromServer() {
        synchronized(listeners)
        {
            for(ClientListener listener : listeners)
                listener.disconnectedFromServer();
        }
    }
}
