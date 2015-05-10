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

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.MarketValue;
import com.flyingblock.acquire.view.CorporationView;
import com.flyingblock.acquire.view.GUIOperations;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

/**
 * Merger Panel that allows a player to trade, sell or keep stocks durring a merger.
 * @author Nicholas Maltbie
 */
public class MergerPanel extends JPanel implements ActionListener
{
    /**
     * Corporations involved in the merger.
     */
    private Corporation parent, child;
    /**
     * Player trading stocks.
     */
    private Investor player;
    /**
     * Buttons to modify the player's current status.
     */
    private JButton tradeIncrease, tradeDecrease, sellIncrease, sellDecrease,
            finishButton;
    /**
     * JLabels to display information to the player.
     */
    private JLabel parentStocks, childStocks, 
            keptStocks, tradedStocks, soldStocks, money, gained, tradeIn;
    /**
     * Views of companies.
     */
    private CorporationView parentCompany, childCompany;
    /**
     * Information about what the player has done during the merger.
     */
    private int traded, sold, sellValue, gainedMoney;
    /**
     * Frame used when the merger is it's own frame.
     */
    private JFrame frame;
    /**
     * Listeners to this class.
     */
    private List<MergerPanelListener> listeners;
    
    /**
     * Default constructor for a merger panel.
     * @param parent Parent company, the one that wins the merger.
     * @param child Child company, the one that lost the merger.
     * @param player Player trading stocks.
     * @param background Background color.
     * @param fontColor Color of the font.
     */
    public MergerPanel(Corporation parent, Corporation child, Investor player, 
            Color background, Color fontColor)
    {
        super();
        this.parent = parent;
        this.child = child;
        this.player = player;
        this.listeners = new ArrayList<>();
        this.setBackground(background);
        
        parentCompany = new CorporationView(parent, "TIMES NEW ROMAN", "TIMES NEW ROMAN",
                Font.BOLD, this.getBackground());
        parentStocks = new JLabel(Integer.toString(player.getStocks(parent)));
        parentStocks.setForeground(fontColor);
        childCompany = new CorporationView(child, "TIMES NEW ROMAN", "TIMES NEW ROMAN",
                Font.BOLD, this.getBackground());
        childStocks = new JLabel(Integer.toString(player.getStocks(child)));
        childStocks.setForeground(fontColor);
        tradeIn = new JLabel("Sell value: $" + Integer.toString(child.getStockPrice()));
        tradeIn.setForeground(fontColor);
        money = new JLabel("Money $" + Integer.toString(player.getMoney()));
        money.setForeground(fontColor);
        gained = new JLabel("$Gained 0");
        gained.setForeground(fontColor);
        keptStocks = new JLabel("Kept: " + player.getStocks(child));
        keptStocks.setForeground(fontColor);
        tradedStocks = new JLabel("Traded: 0");
        tradedStocks.setForeground(fontColor);
        soldStocks = new JLabel("Sold: 0");
        soldStocks.setForeground(fontColor);
        
        tradeIncrease = new JButton("+");
        tradeDecrease = new JButton("-");
        sellIncrease = new JButton("+");
        sellDecrease = new JButton("-");
        finishButton = new JButton("finish");
        
        sellValue = child.getStockPrice();
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.weightx = 1;
        c.weighty = 1;
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.gridy = 0;
        add(money, c);
        c.gridx = 1;
        c.gridwidth = 2;
        add(gained, c);
        
        c.weighty = 2;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 1;
        add(parentCompany, c);
        c.gridx = 2;
        c.gridwidth = 1;
        add(parentStocks, c);
        
        c.weighty = 2;
        c.gridwidth = 2;
        c.gridx = 0;
        c.gridy = 2;
        add(childCompany, c);
        c.gridx = 2;
        c.gridwidth = 1;
        add(childStocks, c);
        
        c.weighty = 1;
        c.gridwidth = 3;
        c.gridx = 0;
        c.gridy = 3;
        //add(keptStocks, c);
        
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 4;
        add(soldStocks, c);
        c.gridx = 1;
        c.insets = new Insets(20,20,20,20);
        add(sellIncrease,c);
        c.gridx = 2;
        add(sellDecrease, c);
        
        c.insets = new Insets(0,0,0,0);
        c.gridx = 0;
        c.gridy = 5;
        add(tradedStocks, c);
        c.gridx = 1;
        c.insets = new Insets(20,20,20,20);
        add(tradeIncrease,c);
        c.gridx = 2;
        add(tradeDecrease, c);
        
        c.insets = new Insets(0,0,0,0);
        c.gridx = 0;
        c.gridy = 6;
        add(tradeIn, c);
        c.gridx = 1;
        c.gridwidth  = 2;
        add(finishButton, c);
    }
    
    /**
     * Adds a listener to this class.
     * @param listener Listener to add.
     */
    public void addListener(MergerPanelListener listener)
    {
        this.listeners.add(listener);
    }
    
    /**
     * Removes a listener.
     * @param listener Listener to remove.
     */
    public void removeListener(MergerPanelListener listener)
    {
        this.listeners.remove(listener);
    }
    
    /**
     * Creates and displays the GUI
     */
    public void setupAndDisplayGUI(Rectangle bounds)
    {
        frame = new JFrame();
        frame.setVisible(true);
        frame.setTitle(parent.getCorporateName() + " Is Consuming " + child.getCorporateName());
        frame.setBounds(bounds);
        frame.setResizable(true);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.setContentPane(this);
        this.repaint();
        finishButton.addActionListener(this);
        sellIncrease.addActionListener(this);
        sellDecrease.addActionListener(this);
        tradeIncrease.addActionListener(this);
        tradeDecrease.addActionListener(this);
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    update();
                }
            }, 100);
    }

    /**
     * Updates the graphical components.
     */
    public void update()
    {
        parentCompany.update();
        childCompany.update();
        childStocks.setText(Integer.toString(player.getStocks(child)));
        parentStocks.setText(Integer.toString(player.getStocks(parent)));
        money.setText("Money $" + Integer.toString(player.getMoney()));
        gained.setText("Gained $" + Integer.toString(gainedMoney));
        keptStocks.setText("Kept: " + player.getStocks(child));
        tradedStocks.setText("Traded: " + Integer.toString(traded));
        soldStocks.setText("Sold: " + Integer.toString(sold));
        
        for(JButton button : new JButton[]{sellIncrease, sellDecrease, tradeIncrease, tradeDecrease})
        {
            button.setFont(new Font(button.getFont().getName(),
            button.getFont().getStyle(), GUIOperations.findFontSize("+",
                button.getFont().getName(),button.getFont().getStyle(),new Rectangle2D.Float(
                    0, 0,button.getWidth()*.75f, button.getHeight()*.75f), this.getGraphics())));
        }
        for(JLabel label : new JLabel[]{keptStocks, tradedStocks, soldStocks, tradeIn, money, gained})
        {
            label.setFont(new Font(label.getFont().getName(),
            label.getFont().getStyle(), GUIOperations.findFontSize("Traded: 99",
                label.getFont().getName(),label.getFont().getStyle(),new Rectangle2D.Float(
                    0, 0,label.getWidth()*.75f, label.getHeight()*.75f), this.getGraphics())));
        }
        for(JLabel label : new JLabel[]{parentStocks, childStocks})
        {
            label.setFont(new Font(label.getFont().getName(),
            label.getFont().getStyle(), GUIOperations.findFontSize("99",
                label.getFont().getName(),label.getFont().getStyle(),new Rectangle2D.Float(
                    0, 0,label.getWidth()*.75f, label.getHeight()*.75f), this.getGraphics())));
        }
    }
    
    @Override
    public void actionPerformed(ActionEvent e) 
    {
        int kept = player.getStocks(child);
        if(e.getSource() == tradeIncrease)
        {
            if(kept >= 2 && parent.getAvailableStocks() >= 1)
            {
                kept -= 2;
                player.addStock(parent.getStock());
                child.returnStock(player.removeStock(child));
                child.returnStock(player.removeStock(child));
                traded += 2;
            }
            update();
        }
        else if(e.getSource() == tradeDecrease)
        {
            if(traded >= 2)
            {
                parent.returnStock(player.removeStock(parent));
                traded -= 2;
                kept += 2;
                player.addStock(child.getStock());
                player.addStock(child.getStock());
            }
            update();
        }
        else if(e.getSource() == sellIncrease)
        {
            if(kept >= 1)
            {
                child.returnStock(player.removeStock(child));
                sold++;
                kept--;
                gainedMoney += sellValue;
                player.addMoney(sellValue);
            }
            update();
        }
        else if(e.getSource() == sellDecrease)
        {
            if(sold >= 1)
            {
                player.addStock(child.getStock());
                sold--;
                kept++;
                gainedMoney -= sellValue;
                player.addMoney(-sellValue);
            }
            update();
        }
        else if(e.getSource() == finishButton)
        {
            synchronized(listeners)
            {
                for(MergerPanelListener l : listeners)
                {
                    l.finished(parent, child, kept, sold, traded);
                }
                frame.dispose();
            }
        }
    }
    
    
    public static void main(String[] args)
    {
        AcquireBoard board = new AcquireBoard();
        Corporation smaller = new Corporation("child", board, MarketValue.LOW, Color.YELLOW, 25);
        Corporation larger = new Corporation("parent", board, MarketValue.HIGH, Color.MAGENTA, 25);
        Investor player = new Investor("Nick", 6000, 6, Color.RED);
        smaller.setHeadquarters(new Location(1,1));
        board.set(1,1, new Hotel(new Location(1,1)));
        board.set(1,2, new Hotel(new Location(1,2)));
        board.set(1,3, new Hotel(new Location(1,3)));
        board.set(1,4, new Hotel(new Location(1,4)));
        smaller.incorporateRegoin();
        for(int i = 0; i < 10; i ++)
            player.addStock(smaller.getStock());
        
        MergerPanel m = new MergerPanel(larger, smaller, player, Color.BLACK, Color.WHITE);
        m.setupAndDisplayGUI(new Rectangle(100,100,700,700));
    }
}
