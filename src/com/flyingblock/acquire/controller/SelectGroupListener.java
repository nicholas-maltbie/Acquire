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

import java.awt.Component;

/**
 * Listener that records events from a SelectGroupItemsPanel.
 * @author Nicholas Maltbie
 */
public interface SelectGroupListener {
    /**
     * When a player has finished selecting items.
     * @param options Options that were selected.
     * @param states ture or false to show if any of the options were selected.
     */
    public void finished(Component[] options, boolean[] states);
}
