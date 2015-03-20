/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.controller;

import com.flyingblock.acquire.model.Corporation;

/**
 * A class that listens to events from a merger panel.
 * @author Nicholas Maltbie
 */
public interface MergerPanelListener 
{
    /**
     * Method called after the player finishes using a merger panel.
     * @param parent Corporation that won the merger.
     * @param child Corporation consumed in the merger.
     * @param kept Stocks kept.
     * @param sold Stocks sold.
     * @param traded Stocks traded in (always multiples of two). To get the stocks
     * that the player gained in the parent company, divide traded by two.
     */
    public void finished(Corporation parent, Corporation child, int kept, 
            int sold, int traded);
}
