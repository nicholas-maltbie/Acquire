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
 * This represents a view for an opposing player as it will have a limited display
 * as it should not show all the opponents data.
 * @author Nicholas Maltbie
 */
public class OtherPlayerView extends JPanel
{
    /**
     * Opponent to display data of.
     */
    private Investor opponent;
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
     * Set to decide if stocks should or shouldn't be displayed. This is just a
     * game setting.
     */
    private boolean displayStocks;
    
    public OtherPlayerView(Investor opponent, List<Corporation> corporatoins, String stockFont,
            String nameFont, String moneyFont, Color background, int nameStyle, int moneyStyle,
            boolean displayStocks)
    {
        this.opponent = opponent;
        this.displayStocks = displayStocks;
        this.setBackground(background);
        money = new JLabel("$" + Integer.toString(opponent.getMoney()));
        money.setFont(new Font(moneyFont, moneyStyle, 10));
        money.setForeground(Color.GREEN.brighter());
        money.setHorizontalAlignment(JLabel.CENTER);
        name = new JLabel(opponent.getName());
        name.setFont(new Font(nameFont, nameStyle, 10));
        name.setForeground(opponent.getColor());
        name.setHorizontalAlignment(JLabel.CENTER);
        display = new StockDisplay(opponent, corporatoins, stockFont, Font.BOLD, background);
        
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
        
        if(displayStocks)
        {
            //c.fill = GridBagConstraints.BOTH;
            c.gridwidth = 2;
            c.weightx = 1;
            c.weighty = 4;
            c.gridx = 0;
            c.gridy = 1;
            this.add(display, c);
        }
    }
    
    /*@Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2 = (Graphics2D) money.getGraphics();
        money.setFont(new Font(money.getFont().getName(), money.getFont().getStyle(), 
                GUIOperations.findFontSize("$" + Integer.toString(opponent.getMoney()), 
                money.getFont().getName(), money.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        money.setText("$" + Integer.toString(opponent.getMoney()));
        
        g2 = (Graphics2D) name.getGraphics();
        name.setFont(new Font(name.getFont().getName(), name.getFont().getStyle(), 
                GUIOperations.findFontSize(opponent.getName(), 
                name.getFont().getName(), name.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        name.setText(opponent.getName());
    }*/
    
    /**
     * Updates the graphical components from the investor that this view
     * is based off.
     */
    public void update()
    {
        Graphics2D g2 = (Graphics2D) money.getGraphics();
        money.setFont(new Font(money.getFont().getName(), money.getFont().getStyle(), 
                GUIOperations.findFontSize("$" + Integer.toString(opponent.getMoney()), 
                money.getFont().getName(), money.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        money.setText("$" + Integer.toString(opponent.getMoney()));
        
        g2 = (Graphics2D) name.getGraphics();
        name.setFont(new Font(name.getFont().getName(), name.getFont().getStyle(), 
                GUIOperations.findFontSize(opponent.getName(), 
                name.getFont().getName(), name.getFont().getStyle(), 
                new Rectangle2D.Float(0,0,getWidth()*.25f,getHeight()*1f/3f), g2)));
        name.setText(opponent.getName());
        this.repaint();
    }
}
