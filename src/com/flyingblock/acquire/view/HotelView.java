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
import com.flyingblock.acquire.model.Location;
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
 * @author Nicholas Maltbie
 */
public class HotelView extends JPanel
{
    /**
     * String used to find optimum text font size. This can be specified to make
     * hotel views in a grid have the same font size.
     */
    private String sizeString;
    /**
     * Represents if this is a view of an empty location.
     */
    private boolean isEmpty;
    /**
     * Color of the text.
     */
    private Color fontColor;
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
     * @param background Background color.
     * @param font font type for the text.
     */
    public HotelView(Hotel hotel, Color background, String font)
    {
        this.hotel = hotel;
        if(hotel.isIncorporated())
            this.fontColor = hotel.getOwner().getColor();
        else
            this.fontColor = Color.GRAY;
        this.background = background;
        this.font = font;
        this.setBackground(background);
    }
    
    /**
     * Constructs a HotelView of an empty location.
     * @param loc Location at which this will be placed/
     * @param background Background color.
     * @param fontColor color of the location text.
     * @param font font type for the text.
     */
    public HotelView(Location loc, Color background, Color fontColor, String font)
    {
        isEmpty = true;
        this.hotel = new Hotel(loc);
        this.fontColor = fontColor;
        this.background = background;
        this.font = font;
        this.setBackground(background);       
    }
    
    /**
     * Sets the sizing string to the specified string. This can be set to null.
     * If there is no sizing string, the HotelView will use the hotel's location
     * text to size the text.
     * @param str Size string, should be a as many characters as the max expected
     * size.
     */
    public void setSizeString(String str)
    {
        this.sizeString = str;
    }
    
    /**
     * Sets the hotel that this view will display.
     * @param hotel New hotel, must not be null.
     */
    public void setHotel(Hotel hotel)
    {
        isEmpty = false;
        this.hotel = hotel;
        this.fontColor = hotel.getOwner().getColor();
    }
    
    /**
     * Sets the hotel to a new empty hotel at a specified location.
     * @param loc Location of the new hotel.
     */
    public void setHotel(Location loc)
    {
        isEmpty = true;
        this.hotel = new Hotel(loc);   
        this.fontColor = Color.BLACK;     
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2 = (Graphics2D) g;
        float width = this.getWidth();
        float height = this.getHeight();
        
        if(!isEmpty)
        {
            RoundRectangle2D rect = new RoundRectangle2D.Float(width*.1f, height*.1f, width*.8f, height*.8f, width*.2f, height*.2f);
            if(hotel.isIncorporated())
                g2.setColor(hotel.getOwner().getColor());
            else
                g2.setColor(Color.GRAY);
            g2.fill(rect);

            RoundRectangle2D rect2 = new RoundRectangle2D.Float(width*.15f, height*.15f, width*.7f, height*.7f, width*.2f, height*.2f);
            if(hotel.isIncorporated())
                g2.setColor(Color.WHITE);
            else
                g2.setColor(Color.BLACK);
            g2.fill(rect2);
        }
        
        String text = hotel.getLocationText();
        if(sizeString != null)
            text = sizeString;
        
        g2.setColor(fontColor);
        Font sizedFont = new Font(font, Font.BOLD, GUIOperations.findFontSize(text, font, 
                Font.BOLD, new Rectangle2D.Float(0,0,width*.7f,height*.7f), g2));
        g2.setFont(sizedFont);
        Rectangle2D fontBounds = g2.getFontMetrics(sizedFont).getStringBounds(hotel.getLocationText(), g2);
        g2.drawString(hotel.getLocationText(), this.getWidth()/2 - (int)fontBounds.getWidth()/2,
                this.getHeight()/2 + (int)fontBounds.getHeight()/4);
    }
}
