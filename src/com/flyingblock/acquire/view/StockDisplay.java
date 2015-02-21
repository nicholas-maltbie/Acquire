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
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.List;
import javax.swing.JPanel;

/**
 * Graphical component that can be used to display an investor's stocks.
 * @author Nicholas Maltbie
 */
public class StockDisplay extends JPanel
{
    /**
     * Player that has the stocks.
     */
    private Investor player;
    /**
     * Companies that the player can own stocks.
     */
    private List<Corporation> companies;
    /**
     * Longest company name used to make all the names appear the same size.
     */
    private String longestName;
    /**
     * Type of font for the text.
     */
    private String font;
    /**
     * Style of the way in which text is displayed.
     */
    private int style;
    
    /**
     * Constructs a stock display that will show a player's stocks.
     * @param player Player to display.
     * @param companies Companies that the player can own stocks.
     * @param font Font to display text.
     * @param style Style of display text.
     */
    public StockDisplay(Investor player, List<Corporation> companies,
            String font, int style)
    {
        this.player = player;
        this.companies = companies;
        this.font = font;
        this.style = style;
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        
        if(longestName == null)
        {
            longestName = companies.get(0).getCorporateName() + ": 99";
            FontMetrics metrics = g.getFontMetrics(new Font(font, style, 10));
            double longest = metrics.getStringBounds(longestName, g).getWidth();
            for(Corporation c : companies)
            {
                String name = c.getCorporateName() + ": 99";
                double width = metrics.getStringBounds(name, g).getWidth();
                if(width > longest)
                {
                    longestName = name;
                    longest = width;
                }
            }
        }
        
        g2.setColor(Color.GREEN.darker().darker());
        int height = this.getHeight();
        
        int titleHeight = (int)(height*.2f);
        int titleSize = GUIOperations.findFontSize("Stocks", font, style, 
                new Rectangle2D.Float(0,0,this.getWidth()*.8f,titleHeight), g2);
        Font titleFont = new Font(font, style, titleSize);
        g2.setFont(titleFont);
        Rectangle2D bounds = g2.getFontMetrics(titleFont).getStringBounds("Stocks", g2);
        g2.drawString("Stocks", this.getWidth()/2 - (int)bounds.getWidth()/2,
                titleHeight/2 + (int)bounds.getHeight()/2*.8f);
        g2.draw(new Line2D.Float(this.getWidth()*.1f, titleHeight, this.getWidth()*.8f, titleHeight));
        
        int stockHeight = (int)(height*.7f/companies.size());
        g2.setFont(new Font(font, style, GUIOperations.findFontSize(longestName, font, style, 
                new Rectangle2D.Float(0,0,this.getWidth()*.8f,height*.7f/companies.size()), g2)));
        
        for(int i = 0; i < companies.size(); i++) {
            g2.setColor(companies.get(i).getColor().darker());
            int stocks = player.getStocks(companies.get(i));
            Rectangle2D nameBounds = g2.getFontMetrics(titleFont).getStringBounds(companies.get(i).getCorporateName() + ": " + stocks, g2);
            g2.drawString(companies.get(i).getCorporateName() + ": " + stocks, this.getWidth()*.1f, (int)(height*.2f)+stockHeight*i+(int)nameBounds.getHeight());
        }
    }
}
