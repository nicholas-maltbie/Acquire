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
import java.awt.Font;
import javax.swing.JButton;
import javax.swing.JLabel;

/**
 * A graphical representation of a company.
 * @author Nicholas Maltbie
 */
public class CorperationView 
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
    private JLabel stockDisplay;
    
    /**
     * Constructs a CorperateView.
     * @param corporation Corporation to display graphically.
     * @param nameFont The font for the corporation name.
     * @param nameStyle The style of the corporation name.
     * @param stockFont The font for the stock count indication.
     * @param stockStyle The style of the stock count indication.
     */
    public CorperationView(Corporation corporation, Font nameFont, 
            int nameStyle, Font stockFont, int stockStyle)
    {
        this.company = corporation;
        buyButton = new JButton("Buy");
        buyButton.setFont(new Font("TIMES NEW ROMAN", Font.BOLD, 10));
        name = new JLabel(corporation.getCorporateName());
        stockDisplay = new JLabel(
                Integer.toString(corporation.getAvailableStocks()));
        /**
         * Note, this class need to be finished later.
         */
    }
    
}
