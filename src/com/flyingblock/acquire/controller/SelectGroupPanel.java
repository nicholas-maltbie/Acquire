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
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.view.CorporationView;
import com.flyingblock.acquire.view.GUIOperations;
import com.flyingblock.acquire.view.HotelView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Panel to buy stocks.
 * @author Nicholas Maltbie
 */
public class SelectGroupPanel extends JPanel implements ActionListener
{
    /**
     * Listeners.
     */
    private List<SelectGroupListener> listeners;
    /**
     * Toggle buttons.
     */
    private JToggleButton[] buttons;
    /**
     * Components to choose from.
     */
    private Component[] components;
    /**
     * Text to show the player for instructions.
     */
    private JLabel displayText;
    /**
     * Close button for when a player si finished buying stocks.
     */
    private JButton closeButton;
    
    /**
     * Constructs a buy SelectGroupPanel.
     * @param choices Components to chose from.
     * @param text Text to display to the user.
     */
    public SelectGroupPanel(Component[] choices, String text)
    {
        super();
        
        components = choices;
        displayText = new JLabel(text);
        closeButton = new JButton("Finish");
        buttons = new JToggleButton[choices.length];
        for(int i = 0; i < buttons.length; i++)
        {
            buttons[i] = new JToggleButton();
        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = choices.length;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        add(displayText, c);
        
        c.gridwidth = 1;
        for(int x = 0; x < choices.length; x++)
        {
            c.gridx = x;
            c.gridy = 1;
            add(buttons[x]);
            
            c.gridy = 2;
            add(choices[x]);
        }
        
        c.gridy = 3;
        c.gridx = 0;
        this.add(closeButton, c);
    }
    
    /**
     * JFrame to display this panel.
     */
    private JFrame frame;
    
    /**
     * Creates and displays the GUI
     */
    public void setupAndDisplayGUI()
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setBounds(100,100,800,800);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(this);
        this.repaint();
        closeButton.addActionListener(this);
    }
    
    /**
     * Listener to add to this class.
     * @param listener Listener to add.
     */
    public void addListener(SelectGroupListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Listener to remove from the current listeners.
     * @param listener Listener to remove.
     */
    public void removeStockListener(SelectGroupListener listener)
    {
        listeners.remove(listener);
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        boolean[] chosen = new boolean[components.length];
        for(int i = 0; i < chosen.length; i++)
            chosen[i] = buttons[i].isSelected();
        if(e.getSource() == closeButton)
        {
            frame.dispose();
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        synchronized(listeners)
                        {
                            for(SelectGroupListener l : listeners)
                            {
                                l.finished(components, chosen);
                            }
                        }
                    }
                }
            , 0);
        }
    }
    
    public static void main(String[] args)
    {
        Hotel h = new Hotel(new Location(10,10));
        Hotel h2 = new Hotel(new Location(10,10));
        Hotel h3 = new Hotel(new Location(10,10));
        SelectGroupPanel panel = new SelectGroupPanel({h,h2,h3}, "try");
    }
}
