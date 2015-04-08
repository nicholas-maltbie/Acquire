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
 * A class that contains information and standards for how Acquire game should
 * communicate with each other.
 * @author Nicholas Maltbie
 */
public class NetworkComms
{
    /**Identifiers to identify game events sent over a network*/
    public static enum EventType {BOARD_UPDATE, PLAYERS_UPDATE, 
        CORPORATIONS_UPDATE};
    
    /**
     * Gets the type of event that the specified event is.
     * @param event GameEvent to check.
     * @return Returns the EventType that the event is identified as or null if
     * the event cannot be identified or if its identification does not fall 
     * within one of the preset identifications.
     */
    public EventType identifyEvent(GameEvent event)
    {
        try {
            return EventType.valueOf(event.getCode());
        }
        catch(IllegalArgumentException e) {
            return null;
        }
    }
    
    public GameEvent createEvent(EventType type, Object message)
    {
        return new GameEvent(type.toString(), message);
    }
}
