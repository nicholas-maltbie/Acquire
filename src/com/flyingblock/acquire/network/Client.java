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

import java.io.IOException;
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
     * @throws java.io.IOException
     */
    public Client(String address, int port) throws IOException {
        super(address, port);
        listeners = new ArrayList<>();
    }
    
    /**
     * Allows a listener to listen to events of the client.
     * @param listener Listener to add.
     */
    public void addListener(ClientListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener from the listeners listening to events.
     * @param listener Listener to remove
     */
    public void removeListener(ClientListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
        }
    }

    @Override
    public void objectRecieved(Object object) {
        synchronized(listeners)
        {
            int i = 0;
            while(i < listeners.size())
            {
                listeners.get(i).objectRecieved(object);
                i++;
            }
        }
    }

    @Override
    public void disconnectedFromServer() {
        synchronized(listeners)
        {
            int i = 0;
            while(i < listeners.size())
            {
                listeners.get(i).disconnectedFromServer();
                i++;
            }
        }
    }
}
