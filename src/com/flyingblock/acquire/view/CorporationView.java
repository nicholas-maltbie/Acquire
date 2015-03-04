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
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

/**
 * A graphical representation of a company.
 * @author Nicholas Maltbie
 */
public class CorporationView extends JPanel implements ActionListener
{
    /**
     * Corporation to graphically represent.
     */
    private Corporation company;
    /**
     * Button for buying stocks.
     */
    private JButton buyButton;
    /**
     * Space for displaying the name.
     */
    private JLabel name;
    /**
     * Space for displaying the number of available stocks.
     */
    private JLabel stockCount;
    /**
     * Listeners to this panel.
     */
    private List<CompanyPanelListener> listeners;
    
    /**
     * Constructs a CorperateView.
     * @param corporation Corporation to display graphically.
     * @param nameFont The font for the corporation name.
     * @param stockFont The font for the stock count indication.
     * @param stockStyle The style of the stock count indication.
     * @param background Background color.
     */
    public CorporationView(Corporation corporation, String nameFont, 
            String stockFont, int stockStyle, Color background)
    {
        this.setBackground(background);
        this.company = corporation;
        listeners = new ArrayList<>();
        
        buyButton = new JButton("Buy");
        buyButton.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 10));
        buyButton.setForeground(background);
        buyButton.setHorizontalAlignment(SwingConstants.CENTER);
        //buyButton.addActionListener(this);
        
        name = new JLabel(corporation.getCorporateName());
        name.setForeground(corporation.getColor());
        name.setHorizontalAlignment(SwingConstants.LEFT);
        
        stockCount = new JLabel(
                Integer.toString(corporation.getAvailableStocks()));
        stockCount.setHorizontalAlignment(SwingConstants.CENTER);
        stockCount.setForeground(new Color(255-background.getRed(),
                255-background.getGreen(),255-background.getBlue()));
        
        GridBagLayout layout = new GridBagLayout();
        GridBagConstraints c = new GridBagConstraints();
        this.setLayout(layout);
        
        c.fill =  GridBagConstraints.BOTH;
        c.gridy = 0;
        c.weightx = 1;
        c.gridwidth = 2;
        this.add(name, c);
        
        c.fill =  GridBagConstraints.BOTH;
        c.gridy = 1;
        c.gridwidth = 1;
        this.add(stockCount, c);
        
        c.fill =  GridBagConstraints.VERTICAL;
        c.gridx = 1;
        c.gridy = 1;
        c.weightx = 2;
        //this.add(buyButton, c);
    }
    
    /**
     * Adds an action listener to the BuyButton.
     * @param listener Listener to add.
     */
    public void addButtonListener(CompanyPanelListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes an action listener to the BuyButton.
     * @param listener listener to remove.
     */
    public void removeButtonListener(CompanyPanelListener listener)
    {
        synchronized(listeners)
        {
            listeners.remove(listener);
        }
    }
    
    /**
     * Gets the corporation that this view represents.
     * @return Returns the corporation that this view represents.
     */
    public Corporation getCompany()
    {
        return company;
    }
    
    /**
     * Updates the view.
     */
    public void update()
    {
        Graphics g = this.getGraphics();
        buyButton.setFont(new Font(buyButton.getFont().getName(), buyButton.getFont().getStyle(), 
                GUIOperations.findFontSize(buyButton.getText(), buyButton.getFont().getName(),
                    buyButton.getFont().getStyle(),new Rectangle2D.Float(
                            0, 0,this.getWidth()*2f/3f*.6f,this.getHeight()/2*.6f), g)));
        int style = 0;
        if(company.isEstablished())
            style = Font.BOLD;
        else
            style = Font.ITALIC;
        name.setFont(new Font(name.getFont().getName(), style, 
                GUIOperations.findFontSize(name.getText(), name.getFont().getName(),
                    name.getFont().getStyle(),new Rectangle2D.Float(
                            0, 0,this.getWidth()*.8f,this.getHeight()/2), g)));
        stockCount.setText(Integer.toString(company.getAvailableStocks()));
        stockCount.setFont(new Font(stockCount.getFont().getName(), stockCount.getFont().getStyle(), 
                GUIOperations.findFontSize(stockCount.getText(), stockCount.getFont().getName(),
                    stockCount.getFont().getStyle(),new Rectangle2D.Float(
                            0, 0,this.getWidth()/3f*.75f,this.getHeight()/2*.75f), g)));
    }

    @Override
    public void actionPerformed(ActionEvent e) 
    {
        synchronized(listeners)
        {
            for(CompanyPanelListener listener : listeners)
                listener.buyButtonPressed(company);
        }
    }
}
