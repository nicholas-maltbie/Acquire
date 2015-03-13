/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.view;

import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Investor;
import java.util.ArrayList;
import java.util.List;

/**
 * Panel to buy stocks.
 * @author Nicholas Maltbie
 */
public class BuyStockPanel
{
    /**
     * Max number of stocks the player can buy.
     */
    private int maxStocks;
    /**
     * Companies to buy stocks from.
     */
    private Corporation[] companies;
    /**
     * Investor who is buying stocks.
     */
    private Investor investor;
    /**
     * The current number of stocks that the player is buying from each company.
     */
    private int[] currentStatus;
    /**
     * Listeners.
     */
    private List<BuyStockPanelListener> listeners;
    
    /**
     * Constructs a buy stock panel listener.
     * @param copmanies Companies.
     * @param investor Investor who is buying stocks.
     * @param maxStocks Max number of stocks the investor can purchase.
     */
    public BuyStockPanel(Corporation[] copmanies, Investor investor, int maxStocks)
    {
        listeners = new ArrayList<>();
    }
    
    /**
     * Listener to add to this class.
     * @param listener Listener to add.
     */
    public void addListener(BuyStockPanelListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Listener to remove from the current listeners.
     * @param listener Listener to remove.
     */
    public void removeStockListener(BuyStockPanelListener listener)
    {
        listeners.remove(listener);
    }
}
