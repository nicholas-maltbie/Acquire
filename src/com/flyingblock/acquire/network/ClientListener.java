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

/**
 * Class that listens to a client's events.
 * @author Nicholas Maltbie
 */
public interface ClientListener
{
    /**
     * Method called whenever the server sends an object to the client.
     * @param object Object received.
     */
    abstract public void objectRecieved(Object object);
    /**
     * Called after the client unexpectedly disconnects from the server.
     */
    abstract public void disconnectedFromServer();
}
