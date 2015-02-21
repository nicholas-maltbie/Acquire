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

import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.geom.Line2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javax.swing.Box;
import javax.swing.JPanel;

/**
 * Graphical component that can be used to display an investor's hand of hotels.
 * @author Nicholas Maltbie
 */
public class HandView extends JPanel implements MouseListener, MouseMotionListener
{
    /**
     * Listeners tracking this class's actions.
     */
    private List<HandListener> listeners;
    /**
     * Investor to display hand pieces.
     */
    private Investor player;
    /**
     * Hotels within the screen.
     */
    private HotelView[] hotels;
    /**
     * Font for the hotels.
     */
    private String font;
    /**
     * Color of gridLines;
     */
    private Color gridColor;
    
    /**
     * Constructs a HandView based on a specified investor.
     * @param player Player to graphically display on the screen.
     * @param font Font of HotelView's to display.
     * @param background Background color of the graphical display.
     * @param gridColor Color of grid lines to separate the hotel icons.
     */
    public HandView(Investor player, String font, Color background, Color gridColor)
    {
        this.player = player;
        this.font = font;
        this.gridColor = gridColor;
        this.hotels = new HotelView[player.getHandSize()];
        this.setLayout(new GridLayout(1,player.getHandSize()));
        
        listeners = new ArrayList<>();
        
        this.setBackground(background);
        
        for(int i = 0; i < player.getHandSize(); i++)
        {
            if(player.getFromHand(i) != null)
            {
                hotels[i] = new HotelView(player.getFromHand(i), this.getBackground(), font);
                this.add(hotels[i]);
            }
            else
            {
                hotels[i] = null;
                this.add(Box.createHorizontalStrut(10));
            }
        }
        
        this.addMouseListener(this);
        this.addMouseMotionListener(this);
    }
    
    /**
     * Updates the hand display from the investor's currently held pieces.
     */
    public void update()
    {        
        for(Component c : this.getComponents())
            this.remove(c);
        
        for(int i = 0; i < player.getHandSize(); i++)
        {
            if(player.getFromHand(i) != null)
            {
                hotels[i] = new HotelView(player.getFromHand(i), this.getBackground(), font);
                this.add(hotels[i]);
            }
            else
            {
                hotels[i] = null;
                this.add(Box.createVerticalBox());
            }            
        }
        //this.validate();
        this.paintComponent(this.getGraphics());
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        
        Graphics2D g2 = (Graphics2D) g;
        int hotelWidth = this.getWidth()/player.getHandSize();
        g2.setColor(gridColor);
        
        for(int i = 0; i <= player.getHandSize(); i++) {
            g2.draw(new Line2D.Float(i*hotelWidth, 0, i*hotelWidth, this.getHeight()));
        }
        g2.draw(new Line2D.Float(0,0,this.getWidth(),0));
        g2.draw(new Line2D.Float(0,this.getHeight(),this.getWidth(),this.getHeight()));
        
        g2.setColor(new Color(255-getBackground().getRed(),
                255-getBackground().getGreen(),255-getBackground().getBlue(), 50));
        if(lastIndex > -1 && lastIndex < player.getHandSize())
            g2.fill(new Rectangle2D.Float(lastIndex*hotelWidth, 0, hotelWidth, this.getHeight()));
    }
    /**
     * Gets the hand location that contains point p.
     * @param p Point to check.
     * @return Returns the index of the hotel that contains p.
     */
    private int getGridLocation(Point p)
    {
        return (int)p.getX()/(this.getWidth()/player.getHandSize());
    }
    
    /**
     * Adds a listener to the board listeners. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to add.
     */
    public void addBoardListener(HandListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener to the board listeners. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to remove.
     * @return If the listener was successfully removed from the list.
     */
    public boolean removeBoardListener(HandListener listener)
    {
        synchronized(listeners)
        {
            return listeners.remove(listener);
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) 
    {
        int index = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.handClicked(index, e);
            });
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        int index = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.handPressed(index, e);
            });
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        int index = getGridLocation(e.getPoint());
        synchronized(listeners)
        {
            listeners.stream().forEach((listener) -> {
                listener.handReleased(index, e);
            });
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        //Nothing goes here becuase nothing should go here.
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        //set last index to -1 to indicate that it has exited the grid.
        lastIndex = -1;
        this.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) 
    {
        //nothing goes in here
    }

    /**
     * The last index of the mouse, -1 or a number greater than the investor's
     * hand size indicates that the mouse is outside of the grid.
     */
    private int lastIndex = -1;
    
    @Override
    public void mouseMoved(MouseEvent e) 
    {
        int index = getGridLocation(e.getPoint());
        if(e.getPoint().getY() < 0 || e.getPoint().getY() >=  this.getHeight())
            index = -1;
        if(lastIndex != index)
        {
            this.repaint();
        }
        lastIndex = index;
    }
}
