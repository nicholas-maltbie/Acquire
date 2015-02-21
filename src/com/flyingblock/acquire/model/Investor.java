/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.model;

import java.util.ArrayList;
import java.util.List;


/**
 * Influences the market. This represents a player in the game.
 * @author Nicholas Maltbie
 */
public class Investor 
{
    /**
     * Name of the investor.
     */
    private String name;
    /**
     * The current balance of a player
     */
    private int money;
    /**
     * Players hold hotels that will be placed on a board. Players can only
     * have a set number of hotels so this represents the hotels that a 
     * player can have;
     */
    private Hotel[] hand;
    /**
     * Player hold stocks that can be bought or sold.
     */
    private List<Stock> stocks;
    
    /**
     * Constructs an investor with a specified amount of starting money, a 
     * maximum hand size and no stocks.
     * @param name Name of the player.
     * @param startingMoney The money that the investor starts with.
     * @param handSize The maximum amount of hotels that a player can hold.
     */
    public Investor(String name, int startingMoney, int handSize)
    {
        this.name = name;
        money = startingMoney;
        hand = new Hotel[handSize];
        stocks = new ArrayList<>();
    }
    
    /**
     * Gets the name of the investor.
     * @return Returns the name field.
     */
    public String getName()
    {
        return name;
    }
    
    /**
     * Gets the maximum number of hotels that this investor can hold.
     * @return Returns the size of the player's hand (including nulls)
     */
    public int getHandSize()
    {
        return hand.length;
    }
    
    /**
     * Sets a hotel that the investor is holding.
     * @param index Index of hand to change.
     * @param hotel Hotel to give the player.
     */
    public void setInHand(int index, Hotel hotel)
    {
        hand[index] = hotel;
    }
    
    /**
     * Removes a piece from the investor's hand.
     * @param index Index to remove from hand.
     * @return Returns the removed Hotel, if empty, will return null
     */
    public Hotel removeFromHand(int index)
    {
        Hotel held = hand[index];
        hand[index] = null;
        return held;
    }
    
    /**
     * Gets a Hotel that the player is holding without changing the investor's
     * hand.
     * @param index Index of the hand to check
     * @return Returns the Hotel found at the index. If empty, will return null.
     */
    public Hotel getFromHand(int index)
    {
        return hand[index];
    }
    
    /**
     * Will fill the investor's hand from the deck. If the deck runs out of cards,
     * the hand will push its hotels to the left.
     * @param deck Deck that hotels are drawn from.
     */
    public void drawFromDeck(HotelMarket deck)
    {
        for(int i = 0; i < getHandSize() && deck.getSize() == 0; i++)
        {
            if(getFromHand(i) == null)
                setInHand(i, deck.draw());
        }
        for(int i = 0; i < getHandSize(); i++)
        {
            if(getFromHand(i) == null && i < getHandSize())
                setInHand(i, getFromHand(i+1));
        }
    }
    
    /**
     * Gets the investor's current balance.
     * @return Returns the amount of money the investor has.
     */
    public int getMoney()
    {
        return money;
    }
    
    /**
     * Adds money to the investor's balance.
     * @param amount Amount of money to add.
     */
    public void addMoney(int amount)
    {
        money += amount;
    }
    
    /**
     * Removes money from the investor's balance.
     * @param amount Amount of money to remove.
     */
    public void removeMoney(int amount)
    {
        money -= amount;
    }
    
    /**
     * Adds a stock to the investor's current stock holdings.
     * @param stock Stock to add to give the investor.
     */
    public void addStock(Stock stock)
    {
        stocks.add(stock);
    }
    
    /**
     * Gets the number of stocks that the investor has.
     * @return Returns the number of stocks that the investor has.
     */
    public int getNumStocks()
    {
        return stocks.size();
    }
    
    /**
     * Gets a stock from the investor's current holding without changing the 
     * investor's holdings.
     * @param index Index of stock to get.
     * @return Returns the stock at the specified index from the player's holding.
     * Will throw an IndexOutOfBoundsException if the index is beyond what the
     * player's holdings.
     */
    public Stock getStock(int index)
    {
        return stocks.get(index);
    }
    
    /**
     * Removes a stock from the investor's holdings.
     * @param index Index of the Stock to remove.
     * @return Returns the removed stock.
     */
    public Stock removeStock(int index)
    {
        return stocks.remove(index);
    }
    
    /**
     * Gets the number of stocks that the investor has for the specified
     * corporation.
     * @param corporation Corporation to check.
     * @return Returns the number of stocks that the investor holds in the
     * corporation.
     */
    public int getStocks(Corporation corporation)
    {
        int num = 0;
        for(Stock s : stocks)
            if(s.getOwner().equals(corporation))
                num++;
        return num;
    }
}