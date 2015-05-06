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

import java.io.Serializable;
import java.util.Arrays;
import java.util.Objects;

/**
 * A message that can be used to code and parse messages sent between servers
 * and clients.
 * @author Nicholas Maltbie
 * @param <E> Type of message
 */
public class GameEvent implements Serializable
{
    /**Code that describes the message*/
    private String code;
    /**Message that is being sent*/
    private Serializable message;
    
    /**
     * Constructs a game event with a code and message.
     * @param code String that describes the message and can be parsed.
     * @param message Message to send.
     */
    public GameEvent(String code, Serializable message)
    {
        this.code = code;
        this.message = message;
    }
    
    /**
     * Gets a code that should describe the object.
     * @return Returns the code put in the message by the sender.
     */
    public String getCode()
    {
        return code;
    }
    
    /**
     * Gets the message included in this event.
     * @return An Object that was sent with the message
     */
    public Serializable getMessage()
    {
        return message;
    }
    
    @Override
    public boolean equals(Object other)
    {
        if(other instanceof GameEvent)
        {
            GameEvent evt = (GameEvent) other;
            return code.equals(evt.getCode()) && message.equals(evt.getMessage());
        }
        return false;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 53 * hash + Objects.hashCode(this.code);
        hash = 53 * hash + Objects.hashCode(this.message);
        return hash;
    }
    
    @Override
    public String toString()
    {
    	if(message instanceof Object[])
    		return code + " " + Arrays.toString((Object[]) message);
    	return code + " " + message;
    }
}
