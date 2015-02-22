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

import com.flyingblock.acquire.model.Investor;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import javax.swing.JPanel;

/**
 * This icon is meant to display a player's current status. This will be 
 * useful for multi player games in which players will be able to see 
 * if another player has disconnected.
 * @author Nicholas Maltbie
 */
public class PlayerStatusIcon extends JPanel
{
    /**
     * Player status that will change how the icon looks.
     */
    public static enum PlayerStatus {WAITING, DISCONNECTED, NOTHING, ACTING, CONNECTING};
    /**
     * Colors to represent the player status.
     */
    public static Color[] statusColors = {Color.MAGENTA, Color.RED, new Color(255, 128, 0), Color.CYAN, Color.YELLOW};
    /**
     * Status of the player.
     */
    private PlayerStatus status;
    /**
     * Player to get data from.
     */
    private Investor player;
    /**
     * Font name for text.
     */
    private String font;
    /**
     * Style of name text.
     */
    private int style;
    
    /**
     * Constructs a PlayerStatusIcon with a starting status.
     * @param status Status of the player.
     * @param player Player to represent.
     * @param font Font name of the player's name text.
     * @param style Style of name text.
     * @param background Background color of the status icon.
     */
    public PlayerStatusIcon(PlayerStatus status, Investor player, String font, 
            int style, Color background)
    {
        this.status = status;
        this.player = player;
        setBackground(background);
    }
    
    /**
     * Sets the current status.
     * @param status New status of the player.
     */
    public void setStatus(PlayerStatus status)
    {
        this.status = status;
        this.repaint();
    }
    
    /**
     * Gets the player this icon represents.
     * @return The player this icon represents.
     */
    public Investor getPlayer()
    {
        return player;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(player.getColor());
        
        int radius = Math.min((int)(getWidth()*.6f/2), getHeight()*3/14);
        int bodyA = (int)(radius*2f);
        int offset = this.getWidth()/2-radius;
        int textHeight = (int)(radius*2/3*1.2f);
        
        g2.setColor(Color.BLACK);
        g2.setFont(new Font(font, style, GUIOperations.findFontSize(player.getName(), font, style, 
                new Rectangle2D.Float(0,0,getWidth(),getHeight()-bodyA*.9f-radius*2 - (getHeight()-bodyA*.9f-radius*2-textHeight)), g2)));
        g2.setColor(Color.WHITE);
        Rectangle2D bounds = g2.getFontMetrics().getStringBounds(player.getName(), g2);
        g2.drawString(player.getName(), getWidth()/2-(int)bounds.getWidth()/2, getHeight()-bodyA*.9f-radius*2);
        
        g2.setColor(statusColors[status.ordinal()]);
        
        //pre coded models and setups for the player status.
        switch(status)
        {
            default:
                g2.fill(new Ellipse2D.Float(offset,getHeight()-bodyA,radius*2,bodyA*2));
                g2.fill(new Ellipse2D.Float(offset,getHeight()-bodyA*.8f-radius*2,radius*2,radius*2));
        }
    }
}
