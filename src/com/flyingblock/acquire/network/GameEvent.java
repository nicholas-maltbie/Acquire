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

/**
 * A message that can be used to code and parse messages sent between servers
 * and clients.
 * @author Nicholas Maltbie
 */
public class GameEvent implements Serializable
{
    /**Code that describes the message*/
    private String code;
    /**Message that is being sent*/
    private Object message;
    
    /**
     * Constructs a game event with a code and message.
     * @param code String that describes the message and can be parsed.
     * @param message Message to send.
     */
    public void GameEvent(String code, Object message)
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
    public Object getMessage()
    {
        return message;
    }
}
