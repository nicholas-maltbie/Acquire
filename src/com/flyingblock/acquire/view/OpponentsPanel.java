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
import com.flyingblock.acquire.view.PlayerStatusIcon.PlayerStatus;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Data panel meant to show other player's data to a user.
 * @author Nicholas Maltbie
 */
public class OpponentsPanel extends JPanel
{
    /**
     * Other player's views.
     */
    private OtherPlayerView[] opponents;
    /**
     * Other player icons to show communication between the players.
     */
    private PlayerStatusIcon[] opponentIcons;
    
    /**
     * Constructs a panel to view opponents in a compact graphical interface.
     * @param opponents Opponents of the current view.
     * @param companies Companies participating in the game.
     * @param background Background color of the panel.
     * @param font Font of all text within the panel.
     * @param style Style of all text within the panel.
     * @param preferredPanelSize The preferred panel size of the OtherPlayerViews
     * within the panel.
     * @param showStocks A decision of whether to show the stocks of opposing 
     * players.
     * @param displayArea Area of the opposing player views to display.
     */
    public OpponentsPanel(Investor[] opponents, List<Corporation> companies,
            Color background, String font, int style, Dimension preferredPanelSize,
            boolean showStocks, Rectangle displayArea)
    {
        int investors = opponents.length;
        this.opponentIcons = new PlayerStatusIcon[investors];
        this.opponents = new OtherPlayerView[investors];
        
        JPanel holdViews = new JPanel();
        holdViews.setBackground(background);
        holdViews.setLayout(new BoxLayout(holdViews, BoxLayout.Y_AXIS));
        holdViews.setAlignmentY(.5f);
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);
        
        for(int i = 0; i < investors; i++)
        {
            opponentIcons[i] = new PlayerStatusIcon(PlayerStatus.NOTHING, 
                    opponents[i], font, style, background);
            this.opponents[i] = new OtherPlayerView(opponents[i], companies,
                    font, font, font, background, style, style, showStocks);
            this.opponents[i].setPreferredSize(preferredPanelSize);
            holdViews.add(this.opponents[i]);
            this.opponents[i].setBorder(BorderFactory.createLineBorder(
                    opponents[i].getColor(), 5, true));
            c.fill = GridBagConstraints.BOTH;
            c.weightx = 1;
            c.weighty = 1;
            c.gridx = i;
            c.gridy = 0;
            this.add(opponentIcons[i], c);
        }
        
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = opponents.length;
        c.weightx = 1;
        c.weighty = .05;
        c.gridx = 0;
        c.gridy = 1;
        Component box = Box.createVerticalStrut(1);
        box.setBackground(Color.BLACK);
        this.add(box, c);
        
        JScrollPane otherViews = new JScrollPane(holdViews, 
                JScrollPane.VERTICAL_SCROLLBAR_ALWAYS, 
                JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        otherViews.setBackground(background);
        otherViews.scrollRectToVisible(displayArea);
        otherViews.setWheelScrollingEnabled(true);
        
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = opponents.length;
        c.weightx = 1;
        c.weighty = 3;
        c.gridx = 0;
        c.gridy = 2;
        this.add(otherViews, c);
    }
    
    /**
     * Sets the status of a player.
     * @param player Player to change status of.
     * @param status New status to set player.
     * @return Returns if this operation was completed successfully.
     */
    public boolean setStatus(Investor player, PlayerStatus status)
    {
        for(PlayerStatusIcon o : opponentIcons)
        {
            if(o.getPlayer().equals(player))
            {
                o.setStatus(status);
                return true;
            }
        }
        return false;
    }
    
    /**
     * Updates the graphical components from the investor that this view
     * is based off.
     */
    public void update()
    {
        for(OtherPlayerView view : opponents)
            view.update();
    }
}
