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
import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JLabel;
import javax.swing.JPanel;

/**
 * Graphical representation of a player. 
 * @author Nicholas Maltbie
 */
public class PlayerView extends JPanel
{
    /**
     * Investor to display on screen.
     */
    private Investor player;
    /**
     * Hand to display on screen.
     */
    private HandView handView;
    /**
     * Display of player's current balance.
     */
    private JLabel money;
    /**
     * Display of player's name.
     */
    private JLabel name;
    /**
     * Display of the player's stocks.
     */
    private StockDisplay display;
    
    /**
     * Constructs a player view of an investor.
     * @param player Player to display on screen.
     * @param corporatoins Corporations that the player will have stocks in. All
     * corporations that the player could buy stocks in.
     * @param stockFont Font type in which stocks will be written.
     * @param nameFont Font type in which investor name is written.
     * @param moneyFont Font type in which money quantity is written.
     * @param handFont The font of hotels in the player's hand.
     * @param background Background color for the this graphical component.
     * @param gridColor Color of grid separators within the hand.
     * @param nameStyle Style for name text.
     * @param moneyStyle style for money text.
     */
    public PlayerView(Investor player, List<Corporation> corporatoins, String stockFont,
            String nameFont, String moneyFont, String handFont, Color background,
            Color gridColor, int nameStyle, int moneyStyle)
    {
        this.player = player;
        this.setBackground(background);
        handView = new HandView(player, handFont, background, gridColor);
        money = new JLabel("$" + Integer.toString(player.getMoney()));
        money.setFont(new Font(moneyFont, moneyStyle, 10));
        money.setForeground(Color.GREEN.brighter());
        money.setHorizontalAlignment(JLabel.CENTER);
        name = new JLabel(player.getName());
        name.setFont(new Font(nameFont, nameStyle, 10));
        name.setForeground(player.getColor());
        name.setHorizontalAlignment(JLabel.CENTER);
        display = new StockDisplay(player, corporatoins, stockFont, Font.BOLD, background);
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);
        
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        this.add(name, c);
        
        //c.fill = GridBagConstraints.BOTH;
        //c.weightx = 1;
        //c.weighty = 1;
        c.gridx = 1;
        //c.gridy = 0;
        this.add(money, c);
        
        //c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 2;
        c.weightx = 1;
        c.weighty = 2;
        c.gridx = 0;
        c.gridy = 1;
        this.add(handView, c);
        
        c.gridwidth = 1;
        c.gridheight = 2;
        c.weightx = 2;
        c.weighty = 1;
        c.gridx = 2;
        c.gridy = 0;
        this.add(display, c);
    }
    
    /**
     * Makes this component listen to parent for events.
     * @param parent Component to listen to.
     */
    public void setupListeners(Container parent)
    {
        handView.setupListener(parent);
    }
    
    /**
     * Removes this component as a listener to the parent.
     * @param parent Must first be listening to this.
     */
    public void closeListener(Container parent)
    {
        handView.closeListener(parent);
    }
    
    /*
    Temporarilly removed
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2 = (Graphics2D) money.getGraphics();
        money.setFont(new Font(money.getFont().getName(), money.getFont().getStyle(), 
                GUIOperations.findFontSize("$" + Integer.toString(player.getMoney()), 
                money.getFont().getName(), money.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        money.setText("$" + Integer.toString(player.getMoney()));
        
        g2 = (Graphics2D) name.getGraphics();
        name.setFont(new Font(name.getFont().getName(), name.getFont().getStyle(), 
                GUIOperations.findFontSize(player.getName(), 
                name.getFont().getName(), name.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        name.setText(player.getName());
    }*/
    
    /**
     * Updates the graphical components from the investor that this view
     * is based off.
     */
    public void update()
    {
        handView.update();
        
        Graphics2D g2 = (Graphics2D) money.getGraphics();
        money.setFont(new Font(money.getFont().getName(), money.getFont().getStyle(), 
                GUIOperations.findFontSize("$" + Integer.toString(player.getMoney()), 
                money.getFont().getName(), money.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        money.setText("$" + Integer.toString(player.getMoney()));
        
        g2 = (Graphics2D) name.getGraphics();
        name.setFont(new Font(name.getFont().getName(), name.getFont().getStyle(), 
                GUIOperations.findFontSize(player.getName(), 
                name.getFont().getName(), name.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        name.setText(player.getName());
        this.repaint();
    }
    
    /**
     * Adds a listener to the hand listeners. Hand listeners listen to mouse
     * actions that are located within the hand.
     * @param listener Listener to add.
     */
    public void addHandLIstener(HandListener listener)
    {
        handView.addHandLIstener(listener);
    }
    
    /**
     * Removes a listener to the hand listeners. Hand listeners listen to mouse
     * actions that are located within the hand.
     * @param listener Listener to remove.
     * @return If the listener was successfully removed from the list.
     */
    public boolean removeHandListener(HandListener listener)
    {
        return handView.removeHandListener(listener);
    }
}
