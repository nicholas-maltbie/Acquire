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

/**
 * A listener that listens to a buy stock panel and its actions.
 * @author Nicholas Maltbie
 */
public interface BuyStockPanelListener 
{
    public void buyingComplete(int[] stocksBought, Corporation[] companies);   
}
