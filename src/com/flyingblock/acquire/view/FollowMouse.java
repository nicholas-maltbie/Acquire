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

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.IllegalComponentStateException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;

/**
 * This will draw a component on a given graphics following the mouse.
 * @author Nicholas Maltbie
 */
public class FollowMouse extends JPanel implements MouseMotionListener
{
    /**
     * Container this is being drawn on.
     */
    private Container parent;
    /**
     * Component that will follow the mouse.
     */
    private Component follow;
    /**
     * Size of the component.
     */
    private Dimension size;
    /**
     * Last known location of the mouse.
     */
    private Point location;
    /**
     * Time left for movement.
     */
    private long timeLeft;
    /**
     * Last time in which the component was moved.
     */
    private long lastTime;
    /**
     * Time of a movement.
     */
    private long totalTime;
    /**
     * Target location to move.
     */
    private Point target;
    /**
     * Initial point of the component.
     */
    private Point intial;
    /**
     * saved position of component as floats.
     */
    private float componentX, componentY;
    /**
     * Time delay between request to start following the mouse and 
     * the actual start;
     */
    private int delay;
    /**
     * Listeners to this FollowerMouse.
     */
    private List<FollowMouseListener> listeners;
    
    
    /**
     * Constructs a mouse following panel. It starts NOT following the mouse. 
     * Starting the component at point (0,0).
     * @param follow Component to follow the mouse.
     * @param size Dimensions of the component.
     * @param delay Transition length when the component starts following the cursor.
     * @param parent Container this is being drawn on.
     */
    public FollowMouse(Container parent, Component follow, Dimension size, int delay)
    {
        this(parent, follow, size, delay, null);
    }
    
    /**
     * Constructs a mouse following panel. It starts NOT following the mouse.
     * @param follow Component to follow the mouse.
     * @param size Dimensions of the component.
     * @param delay Transition length when the component starts following the cursor.
     * @param startingLocation Starting center for the component. If startingLocation
     * is null, it will start at point (0,0).
     * @param parent Container this is being drawn on.
     */
    public FollowMouse(Container parent, Component follow, Dimension size, int delay, Point startingLocation)
    {
        this.parent = parent;
        this.follow = follow;
        this.size = size;
        this.delay = delay;
        this.follow.setPreferredSize(size);
        if(startingLocation != null)
            location = startingLocation;
        else
            location = new Point(0,0);
        follow.setLocation(location);
        lastTime = System.currentTimeMillis();
        listeners = new ArrayList<>();
        this.add(follow, JLayeredPane.DRAG_LAYER);
    }
    
    /**
     * This will stop the component from following the mouse.
     */
    public void pause()
    {
        if(new ArrayList<>(Arrays.asList(parent.getMouseMotionListeners())).
                contains(this))
            parent.removeMouseMotionListener(this);
    }
    
    /**
     * This resumes or starts the component to follow the mouse.
     */
    public void startFolowing()
    {
        if(!new ArrayList<>(Arrays.asList(parent.getMouseMotionListeners())).
                contains(this))
        {
            parent.addMouseMotionListener(this);
            Point mouse = MouseInfo.getPointerInfo().getLocation();
            try{
                Point screen = parent.getLocationOnScreen();
                moveComponent(new Point(mouse.x - screen.x, mouse.y - screen.y), delay);
            }
            catch(IllegalComponentStateException e)
            {
                return;
            }
            
        }
    }
    
    /**
     * Will pause/resume the component following the mouse.
     */
    public void toggle()
    {
        if(new ArrayList<>(Arrays.asList(parent.getMouseMotionListeners())).
                contains(this))
            this.pause();        
        else
            this.startFolowing();            
    }
    
    /**
     * Starts moving the component to the target location over time. This also
     * pauses the movement of the mouse from the cursor.
     * @param target Target location for the component (center of the component).
     * @param time Time (in milliseconds) for the component to move to the target.
     */
    public void moveComponent(Point target, long time)
    {
        timeLeft = time;
        totalTime = time;
        this.target = target;
        this.intial = new Point(this.getX() + size.width/2, this.getY() +
                size.height/2);
        componentX = (float)intial.getX();
        componentY = (float)intial.getY();
        lastTime = System.currentTimeMillis();
        this.repaint();
    }
    
    /**
     * Adds a FollowMouseListener to the current listeners of this class.
     * @param listener Listeners to add.
     */
    public void addListener(FollowMouseListener listener)
    {
        listeners.add(listener);
    }
    
    /**
     * Removes a FollowMouseListener from the current listeners of this class.
     * @param listener Listener to remove.
     */
    public void removeListener(FollowMouseListener listener)
    {
        listeners.remove(listener);
    }
    
    /**
     * Changes the component being drawn to c.
     * @param c New component to be drawn.
     */
    public void setComponent(Component c)
    {
        this.remove(follow);
        this.follow = c;
        this.repaint();
    }
    
    /**
     * Removes the component from the panel.
     */
    public void removeComponent()
    {
        this.remove(follow);
        this.repaint();
    }
    
    /**
     * Sets the bounds for the component following the mouse.
     * @param size New width and height for the component.
     */
    public void setFollowSize(Dimension size)
    {
        this.size = size;
        follow.setPreferredSize(size);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        long time = System.currentTimeMillis();
        Point draw = location;
        if(target != null)
        {
            long elapsed = time - lastTime;
            
            if(elapsed >= timeLeft)
            {
                timeLeft = 0;
                draw = new Point(target);
                location = draw;
                target = null;
                
                new java.util.Timer().schedule( 
                    new java.util.TimerTask() {
                        @Override
                        public void run() {
                            synchronized(listeners)
                            {
                                for(FollowMouseListener listener : listeners)
                                    listener.finishedMove(target);
                            }
                        }
                    }, 10);
            }
            else
            {
                float percent = (float)elapsed / totalTime;
                componentX += percent * (float)(target.getX() - this.intial.getX());
                componentY += percent * (float)(target.getY() - this.intial.getY());
                draw = new Point((int) componentX, (int) componentY);
            }
            
            timeLeft -= elapsed;
            
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        repaint();
                    }
                }, 
                10);
        }
        System.out.println("red");
        this.setLocation(draw.x - size.width/2, draw.y - size.height/2);
        //follow.setLocation(draw.x - size.width/2, draw.y - size.height/2);
        lastTime = time;
    }
    
    /**
     * Retrieves the bounds for the moving component.
     * @return Returns the component's bounds.
     */
    public Rectangle getComponentBounds()
    {
        return follow.getBounds();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        location = e.getPoint();
        this.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        location = e.getPoint();
        this.repaint();
    }
    
}
