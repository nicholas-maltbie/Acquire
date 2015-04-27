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

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Board;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Stock;

/**
 * A class that contains information and standards for how Acquire game should
 * communicate with each other.
 * @author Nicholas Maltbie
 */
public enum EventType
{
    /**Identifiers to identify game events sent over a network*/
    
    //updates sent to clients
    BOARD_UPDATE(AcquireBoard.class),
    PLAYERS_UPDATE(Investor[].class), 
    CORPORATIONS_UPDATE(Corporation[].class),
    
    GAME_START(Object[].class), //in format {Investor[], AcquireBoard, Corporation[]}
    
    //information sent to the server
    CHAT_SEND(String.class),
    PIECE_PLAYED(Hotel.class),
    STOCKS_BOUGHT(Stock[].class),
    STOCKS_SOLD(Stock[].class),
    MERGER_ACTION(int[].class), //in format {sold, traded}
    CORPORATION_CREATED(Corporation.class),
    MERGER_WINNER(Corporation.class),
    MERGED_FIRST(Corporation.class),
    
    //prompts sent to clients to get information
    PLAY_PIECE(Hotel[].class),
    BUY_STOCKS(Corporation[].class),
    SELL_STOCKS(Stock[].class),
    TAKE_MERGER(Corporation[].class), //in format {parent, child}
    CREATE_CORPORATION(Corporation[].class),
    CHOOSE_WINNER(Corporation[].class),
    CHOOSE_FIRST(Corporation[].class),
    
    //prmpts sent to server to get information
    GET_BOARD(Object.class),            //
    GET_PLAYERS(Object.class),          //No useful data is passed here
    GET_CORPORATIONS(Object.class),     //
    
    //necessary events
    INVALID_RESPONSE(GameEvent.class)
    ;
    
    private final Class messageType;
    
    private EventType(Class messageType)
    {
        this.messageType = messageType;
    }
    
    /**
     * Gets the type of event that the specified event is.
     * @param event GameEvent to check.
     * @return Returns the EventType that the event is identified as or null if
     * the event cannot be identified or if its identification does not fall 
     * within one of the preset identifications.
     */
    public static EventType identifyEvent(GameEvent event)
    {
        try {
            return EventType.valueOf(event.getCode());
        }
        catch(IllegalArgumentException e) {
            return null;
        }
    }
    
    /**
     * Creates an event.
     * @param <E> Type of event that aligns with the EventType
     * @param type Type of event.
     * @param message Message to attach to the event.
     * @return Returns a new GameEvent.
     */
    public static <E> GameEvent<E> createEvent(EventType type, E message)
    {
        return new GameEvent(type.toString(), message);
    }
    
    /**
     * Returns if an event contains the correct data in its message.
     * @param event Event that is being checked.
     * @return Returns if the event's message Class is the correct class for
     * the specific event type.
     */
    public static boolean isValid(GameEvent event)
    {
        EventType type = identifyEvent(event);
        return type != null && type.getEventClass().equals(event.getMessage().getClass());
    }
    
    /**
     * Gets the class of message that should be sent with a specified event.
     * @return Returns the Class that the message should be.
     */
    public Class getEventClass()
    {
        return messageType;
    }
}