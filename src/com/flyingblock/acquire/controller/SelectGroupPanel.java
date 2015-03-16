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

import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.view.HotelView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
     * Text to display on the buttons.
     */
    private String selectedText, unSelectedText;
    
    /**
     * Constructs a buy SelectGroupPanel.
     * @param choices Components to chose from.
     * @param text Text to display to the user.
     * @param selectedText Text to display when a button is selected.
     * @param unSelectedText Text to display when a button is not selected.
     */
    public SelectGroupPanel(Component[] choices, String text, String selectedText,
            String unSelectedText)
    {
        super();
        this.selectedText = selectedText;
        this.unSelectedText = unSelectedText;
        listeners = new ArrayList<>();
        components = choices;
        displayText = new JLabel(text);
        closeButton = new JButton("Finish");
        buttons = new JToggleButton[choices.length];
        for(int i = 0; i < buttons.length; i++)
        {
            buttons[i] = new JToggleButton();
            buttons[i].setText(unSelectedText);
        }
        
        setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill = GridBagConstraints.NONE;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = choices.length;
        c.anchor = GridBagConstraints.CENTER;
        c.gridheight = 1;
        c.weightx = 1;
        c.weighty = 1;
        add(displayText, c);
        displayText.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 30));
        
        c.fill = GridBagConstraints.BOTH;
        c.gridwidth = 1;
        for(int x = 0; x < choices.length; x++)
        {
            c.insets = new Insets(20,20,20,20);
            c.gridx = x;
            c.gridy = 1;
            add(buttons[x], c);
            
            c.gridy = 2;
            choices[x].setMinimumSize(new Dimension(100,100));
            add(choices[x], c);
        }
        
        c.gridy = 3;
        c.gridx = 0;
        c.gridwidth = choices.length;
        this.add(closeButton, c);
    }
    
    /**
     * JFrame to display this panel.
     */
    private JFrame frame;
    
    /**
     * Creates and displays the GUI
     */
    public void setupAndDisplayGUI(Rectangle bounds)
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setBounds(bounds);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(this);
        this.repaint();
        closeButton.addActionListener(this);
        for(JToggleButton b : buttons)
        {
            b.addActionListener(this);
            b.setMinimumSize(b.getSize());
        }
        for(Component c : components)
        {
            c.setMinimumSize(c.getSize());
        }
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
        else
        {
            for(JToggleButton b : buttons)
            {
                if(b.isSelected())
                    b.setText(selectedText);
                else
                    b.setText(unSelectedText);
            }
        }
    }
    
    public static void main(String[] args)
    {
        HotelView h1 = new HotelView(new Hotel(new Location(10,10)), Color.BLACK, "TIMES NEW ROMAN");
        HotelView h2 = new HotelView(new Hotel(new Location(10,10)), Color.BLACK, "TIMES NEW ROMAN");
        HotelView h3 = new HotelView(new Hotel(new Location(10,10)), Color.BLACK, "TIMES NEW ROMAN");
        
        HotelView[] hotels = {h1,h2,h3};
        SelectGroupPanel panel = new SelectGroupPanel(hotels, "<html>You cannot play these tiles,<br> do you want to keep any?</html>", "KEEP", "REPLACE");
        panel.setupAndDisplayGUI(new Rectangle(100,100,500,500));
        panel.addListener((Component[] choices, boolean[] selected) -> {
            System.out.println(Arrays.toString(choices));
            System.out.println(Arrays.toString(selected));
            System.exit(0);
        });
    }
}
