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

import com.flyingblock.acquire.model.Hotel;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import javax.swing.JPanel;

/**
 * Provides a graphical representation of a hotel.
 * @author Nick_Pro
 */
public class HotelView extends JPanel
{
    /**
     * Hotel that this class will represent.
     */
    private Hotel hotel;
    /**
     * Background and text color.
     */
    private Color background;
    /**
     * Font for the view.
     */
    private String font;
    
    /**
     * Constructs a HotelView.
     * @param hotel Hotel that this view will represent.
     * @param background Background and text color.
     * @param font font for the text.
     */
    public HotelView(Hotel hotel, Color background, String font)
    {
        this.hotel = hotel;
        this.background = background;
        this.font = font;
        this.setBackground(background);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        float width = this.getWidth();
        float height = this.getHeight();
        RoundRectangle2D rect = new RoundRectangle2D.Float(width*.05f, height*.05f, width*.9f, height*.9f, width*.2f, height*.2f);
        if(hotel.isIncorporated())
            g2.setColor(hotel.getOwner().getColor());
        else
            g2.setColor(Color.GRAY);
        g2.fill(rect);
        
        RoundRectangle2D rect2 = new RoundRectangle2D.Float(width*.1f, height*.1f, width*.8f, height*.8f, width*.2f, height*.2f);
        g2.setColor(background);
        g2.fill(rect2);
        
        String text = hotel.getLocationText();
        Rectangle2D targetBounds = new Rectangle2D.Float(0,0,width*.8f,height*.8f);
        
        int fontSize = 2;
        FontMetrics metrics = g2.getFontMetrics(new Font(font, Font.BOLD, fontSize));
        Rectangle2D fontBounds = metrics.getStringBounds(text, g2);
        Rectangle2D bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        while(targetBounds.contains(bounds))
        {
            fontSize *= 2;
            metrics = g2.getFontMetrics(new Font(font, Font.BOLD, fontSize));
            fontBounds = metrics.getStringBounds(text, g2);
            bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        }
        while(!targetBounds.contains(bounds))
        {
            fontSize--;
            metrics = g2.getFontMetrics(new Font(font, Font.BOLD, fontSize));
            fontBounds = metrics.getStringBounds(text, g2);
            bounds = new Rectangle2D.Float(0,0,(float)fontBounds.getWidth(), (float)fontBounds.getHeight());
        }
        g2.setColor(hotel.getOwner().getColor());
        g2.setFont(new Font(font, Font.BOLD, fontSize));
        fontBounds = metrics.getStringBounds(text, g2);
        g2.drawString(text, this.getWidth()/2 - (int)fontBounds.getWidth()/2, this.getHeight()/2 + (int)fontBounds.getHeight()/4);
    }
    
}
