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
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.CorporationView;
import com.flyingblock.acquire.view.GUIOperations;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
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

/**
 * Panel to buy stocks.
 * @author Nicholas Maltbie
 */
public class BuyStockPanel extends JPanel implements ComponentListener, ActionListener
{
    /**
     * Max number of stocks the player can buy.
     */
    private int maxStocks;
    /**
     * Companies to buy stocks from.
     */
    private Corporation[] companies;
    /**
     * Investor who is buying stocks.
     */
    private Investor investor;
    /**
     * The current number of stocks that the player is buying from each company.
     */
    private int[] currentStatus;
    /**
     * Listeners.
     */
    private List<BuyStockPanelListener> listeners;
    /**
     * Buy sell buttons.
     */
    private JButton[][] buttons;
    /**
     * Current company prices.
     */
    private JLabel[][] companyData;
    /**
     * Company views.
     */
    private CorporationView[] views;
    /**
     * Money the player has.
     */
    private JLabel money;
    /**
     * the extra stocks that the player can buy.
     */
    private JLabel left;
    /**
     * Close button for when a player si finished buying stocks.
     */
    private JButton closeButton;
    
    /**
     * Constructs a buy stock panel listener.
     * @param copmanies Companies.
     * @param investor Investor who is buying stocks.
     * @param maxStocks Max number of stocks the investor can purchase.
     * @param stockFont Font of stock names.
     * @param nameFont Font for company names.
     * @param background Background color.
     */
    public BuyStockPanel(Corporation[] copmanies, Investor investor, 
            int maxStocks, String stockFont, String nameFont, Color background)
    {
        super();
        this.maxStocks = maxStocks;
        this.companies = copmanies;
        this.investor = investor;
        this.setBackground(background);
        listeners = new ArrayList<>();
        
        buttons = new JButton[companies.length][2];
        companyData = new JLabel[companies.length][5];
        
        this.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        this.currentStatus = new int[companies.length];
        this.views = new CorporationView[companies.length];
        this.setBackground(background);
        
        money = new JLabel("Money $" + investor.getMoney());
        left = new JLabel("Left: " + getBuysLeft());
        
        Color textColor = new Color(255-background.getRed(), 255-background.getGreen(),
                255-background.getBlue());
        
        c.fill = GridBagConstraints.BOTH;
        c.gridx = 0;
        c.weightx = 1;
        c.weighty = 10;
        c.gridwidth = 2;
        this.add(money, c);
        money.setForeground(textColor);
        
        c.gridx = 2;
        c.gridwidth = 3;
        this.add(left, c);
        left.setForeground(textColor);
        
        
        for(int i = 0; i < copmanies.length; i++)
        {
            c.weighty = 1;
            c.fill = GridBagConstraints.BOTH;
            c.gridx = 0;
            c.gridy = 1+i*3;
            c.gridwidth = 1;
            c.gridheight = 3;
            c.weightx = 2;
            views[i] = new CorporationView(companies[i], nameFont,
                    stockFont, Font.BOLD,background);
            this.add(views[i], c);
            Color border = textColor;
            if(!companies[i].isEstablished())
                border = background;
            views[i].setBorder(BorderFactory.createLineBorder(Color.WHITE, 3));
            companyData[i][0] = new JLabel("Price $" + Integer.toString(copmanies[i].getStockPrice()));
            companyData[i][1] = new JLabel("MIN $" + Integer.toString(companies[i].getMinorityBonus()));
            companyData[i][2] = new JLabel("MAJ $" + Integer.toString(companies[i].getMajorityBonus()));
            
            c.weightx = 1;
            
            c.gridheight = 1;
            c.gridx = 1;
            this.add(companyData[i][0], c);
            companyData[i][0].setForeground(textColor);
            companyData[i][0].setAlignmentX(.5f);
            c.gridy = i*3 + 2;
            this.add(companyData[i][1], c);
            companyData[i][1].setForeground(textColor);
            companyData[i][1].setAlignmentX(.5f);
            c.gridy = i*3 + 3;
            this.add(companyData[i][2], c);
            companyData[i][2].setForeground(textColor);
            companyData[i][2].setAlignmentX(.5f);
            
            c.weightx = 1;
            
            buttons[i][0] = new JButton("+");
            buttons[i][1] = new JButton("-");
            c.insets = new Insets(10,10,10,10);
            buttons[i][0].setActionCommand("ADD"+","+companies[i].getCorporateName());
            buttons[i][1].setActionCommand("REMOVE"+","+companies[i].getCorporateName());
            
            c.gridy = 1+i*3;
            c.gridheight = 3;
            c.gridx = 2;
            add(buttons[i][0], c);
            c.gridx = 3;
            add(buttons[i][1], c);
            
            c.insets = new Insets(0,0,0,0);
            
            companyData[i][3] = new JLabel(Integer.toString(currentStatus[i]));
            companyData[i][3].setForeground(border);
            companyData[i][3].setAlignmentX(.5f);
            c.gridx = 4;
            this.add(companyData[i][3], c);
            
            companyData[i][4] = new JLabel(Integer.toString(investor.getStocks(companies[i])));
            companyData[i][4].setForeground(companies[i].getColor());
            companyData[i][4].setAlignmentX(.5f);
            c.gridx = 5;
            this.add(companyData[i][4], c);
        }
        
        closeButton = new JButton("Finish");
        c.fill = GridBagConstraints.BOTH;
        c.gridy = companies.length*3 + 1;
        c.gridx = 2;
        c.gridwidth = 3;
        this.add(closeButton, c);
    }
    
    /**
     * Gets the number of stocks that the player can still buy.
     * @return Returns the number of stocks that the player can buy.
     */
    private int getBuysLeft()
    {
        int all = 0;
        for(int i = 0; i < currentStatus.length; i++)
            all += currentStatus[i];
        return maxStocks - all;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setColor(Color.WHITE);
        for(int i = 0; i < companies.length; i++)
        {
            Rectangle bounds = views[i].getBounds();
            g2.draw(new Line2D.Float(0, (float)bounds.getY(), this.getWidth(),
                    (float)bounds.getY()));
            g2.draw(new Line2D.Float(0, (float)(bounds.getY()+bounds.getHeight()),
                    this.getWidth(), (float)(bounds.getY()+bounds.getHeight())));
        }
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
        this.lastBounds = frame.getBounds();
        frame.setContentPane(this);
        this.repaint();
        for(int i = 0; i < 10; i++)
            this.update();
        frame.addComponentListener(this);
        for(int i = 0; i < companies.length; i++)
        {
            buttons[i][0].addActionListener(this);
            buttons[i][1].addActionListener(this);
        }
        closeButton.addActionListener(this);
    }
    
    /**
     * update the GUI components.
     */
    public final void update()
    {
        left.setText("Left: " + getBuysLeft());
        money.setText("Money $" + investor.getMoney());
        
        left.setFont(new Font(left.getFont().getName(),
                left.getFont().getStyle(), GUIOperations.findFontSize("left: 999",
                        left.getFont().getName(),left.getFont().getStyle(),new Rectangle2D.Float(
                            0, 0,left.getWidth()*.8f, left.getHeight()*.8f), this.getGraphics())));
        money.setFont(new Font(money.getFont().getName(),
                money.getFont().getStyle(), GUIOperations.findFontSize("99999",
                        money.getFont().getName(),money.getFont().getStyle(),new Rectangle2D.Float(
                            0, 0,money.getWidth()*.8f, money.getHeight()*.8f), this.getGraphics())));
        for(JButton[] row : buttons)
            for(JButton button : row)
                button.setFont(new Font(button.getFont().getName(),
                        button.getFont().getStyle(), GUIOperations.findFontSize("+",
                            button.getFont().getName(),button.getFont().getStyle(),new Rectangle2D.Float(
                                0, 0,button.getWidth()*.8f, button.getHeight()*.8f), this.getGraphics())));
        
        for(int i = 0; i < companies.length; i++)
        {
            views[i].update();
            companyData[i][3].setText(Integer.toString(currentStatus[i]));
            companyData[i][4].setText(Integer.toString(investor.getStocks(companies[i])));
            
            for(int j = 0; j < companyData[i].length - 1; j++)
            {
                companyData[i][j].setFont(new Font(companyData[i][j].getFont().getName(),
                        companyData[i][j].getFont().getStyle(), 
                        GUIOperations.findFontSize("MAJ $99999",
                                companyData[i][j].getFont().getName(),companyData[i][j].getFont().getStyle(),new Rectangle2D.Float(
                                    0, 0,companyData[i][j].getWidth()*.8f, companyData[i][j].getHeight()*.8f), this.getGraphics())));
            }
            int j = companyData[i].length-2;
            while(j < companyData.length - 2)
            {
                companyData[i][j].setFont(new Font(companyData[i][j].getFont().getName(),
                    companyData[i][j].getFont().getStyle(), 
                    GUIOperations.findFontSize("9",
                            companyData[i][j].getFont().getName(),companyData[i][j].getFont().getStyle(),new Rectangle2D.Float(
                                0, 0,companyData[i][j].getWidth()*.8f, companyData[i][j].getHeight()*.8f), this.getGraphics())));
                j++;
            }
        }
    }
    
    /**
     * Listener to add to this class.
     * @param listener Listener to add.
     */
    public void addListener(BuyStockPanelListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Listener to remove from the current listeners.
     * @param listener Listener to remove.
     */
    public void removeStockListener(BuyStockPanelListener listener)
    {
        listeners.remove(listener);
    }
    
    @Override
    public void componentResized(ComponentEvent e) {
        if(e.getSource() == frame)
        {
            this.update();
            lastBounds = frame.getBounds();
        }
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
        if(!frame.getBounds().equals(lastBounds))
            componentResized(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //do nothing
    }
    /**
     * Last bounds of the window.
     */
    private Rectangle lastBounds;

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        List<JButton> actionButtons = new ArrayList<>();
        for(int i = 0; i < buttons.length; i++)
            for(int j = 0; j < buttons[i].length; j++)
                actionButtons.add(buttons[i][j]);
        if(actionButtons.contains(e.getSource()))
        {
            int change = 1;
            String action = e.getActionCommand().split(",")[0];
            if(action.equals("REMOVE"))
                change = -1;
            
            if(change == 1 && getBuysLeft() <= 0)
                return;
            String company = e.getActionCommand().split(",")[1];
            for(int i = 0; i < companies.length; i++)
            {
                if(companies[i].getCorporateName().equals(company))
                {
                    if(change == -1 && currentStatus[i] <= 0)
                        return;
                    else
                    {
                        if(change == 1 && (!(investor.getMoney() >= companies[i].getStockPrice()) ||
                                companies[i].getAvailableStocks() <= 0))
                            return;
                        else if(companies[i].isEstablished())
                        {
                            currentStatus[i] += change;
                            investor.addMoney(-change*companies[i].getStockPrice());
                            if(change == 1)
                                investor.addStock(companies[i].getStock());
                            else
                                companies[i].returnStock(investor.removeStock(companies[i]));
                        }
                    }
                }
            }
            this.update();
        }
        if(e.getSource() == closeButton)
        {
            frame.dispose();
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        synchronized(listeners)
                        {
                            for(BuyStockPanelListener l : listeners)
                            {
                                l.buyingComplete(currentStatus, companies);
                            }
                        }
                    }
                }
            , 0);
        }
    }
}
