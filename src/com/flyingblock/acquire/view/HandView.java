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
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
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
     * Parent this is drawn on and will listen to.
     */
    private Container parent;
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
    }
    
    /**
     * Makes this component listen to parent for events.
     * @param parent Component to listen to.
     */
    public void setupListener(Container parent)
    {
        parent.addMouseListener(this);
        parent.addMouseMotionListener(this);
        this.parent = parent;
    }
    
    /**
     * Removes this component as a listener to the parent.
     * @param parent Must first be listening to this.
     */
    public void closeListener(Container parent)
    {
        parent.removeMouseListener(this);
        parent.removeMouseMotionListener(this);
        this.parent = this;
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
     * Adds a listener to the hand listeners. Hand listeners listen to mouse
     * actions that are located within the hand.
     * @param listener Listener to add.
     */
    public void addHandLIstener(HandListener listener)
    {
        synchronized(listeners)
        {
            listeners.add(listener);
        }
    }
    
    /**
     * Removes a listener to the hand listeners. Hand listeners listen to mouse
     * actions that are located within the hand.
     * @param listener Listener to remove.
     * @return If the listener was successfully removed from the list.
     */
    public boolean removeHandListener(HandListener listener)
    {
        synchronized(listeners)
        {
            return listeners.remove(listener);
        }
    }

    /**
     * Gets where this point is found on the screen or null if the point is not 
     * within the JPanel.
     * @param onScreen Point on the screen.
     * @return Returns Point within the JPanel or null if it is not within the
     * JPanel.
     */
    private Point getRelativeToThis(Point onScreen)
    {
        Point screen = this.getLocationOnScreen();
        Rectangle bounds = new Rectangle(screen.x, screen.y,
                this.getWidth(), this.getHeight());
        if(!bounds.contains(onScreen))
            return null;
        return new Point(onScreen.x - screen.x, onScreen.y - screen.y);
    }
    
    @Override
    public void mouseClicked(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            int index = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.handClicked(index, e);
                });
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            int index = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.handPressed(index, e);
                });
            }
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            int index = getGridLocation(relative);
            synchronized(listeners)
            {
                listeners.stream().forEach((listener) -> {
                    listener.handReleased(index, e);
                });
            }
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
        parent.repaint();
    }

    /**
     * The last index of the mouse, -1 or a number greater than the investor's
     * hand size indicates that the mouse is outside of the grid.
     */
    private int lastIndex = -1;
    
    @Override
    public void mouseMoved(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            int index = getGridLocation(relative);
            if(relative.getY() < 0 || relative.getY() >=  this.getHeight())
                index = -1;
            if(lastIndex != index)
            {
                parent.repaint();
            }
            lastIndex = index;
        }
        else
        {
            lastIndex = -1;
            parent.repaint();
        }
            
    }
    
    @Override
    public void mouseDragged(MouseEvent e) 
    {
        Point relative = getRelativeToThis(e.getLocationOnScreen());
        if(relative != null)
        {
            int index = getGridLocation(relative);
            if(relative.getY() < 0 || relative.getY() >=  this.getHeight())
                index = -1;
            if(lastIndex != index)
            {
                parent.repaint();
            }
            lastIndex = index;
        }
        else
        {
            lastIndex = -1;
            parent.repaint();
        }
    }
}
