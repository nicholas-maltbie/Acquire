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

import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JPanel;

/**
 * This will draw a component on a given graphics following the mouse.
 * @author Maltbie_N
 */
public class FollowMouse extends JPanel implements MouseMotionListener
{
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
     * Constructs a mouse following panel. It starts following the mouse.
     * @param follow Component to follow the mouse.
     * @param size Dimensions of the component.
     */
    public FollowMouse(Component follow, Dimension size)
    {
        this.follow = follow;
        this.size = size;
        follow.setPreferredSize(size);
        this.addMouseMotionListener(this);
        location = new Point(0,0);
        lastTime = System.currentTimeMillis();
        this.add(follow);
    }
    
    /**
     * This will stop the component from following the mouse.
     */
    public void pause()
    {
        if(new ArrayList<>(Arrays.asList(this.getMouseMotionListeners())).
                contains(this))
            this.removeMouseMotionListener(this);
    }
    
    /**
     * This resumes or starts the component to follow the mouse.
     */
    public void startFolowing()
    {
        if(!new ArrayList<>(Arrays.asList(this.getMouseMotionListeners())).
                contains(this))
        {
            this.addMouseMotionListener(this);
        }
    }
    
    /**
     * Will pause/resume the component following the mouse.
     */
    public void toggle()
    {
        if(new ArrayList<>(Arrays.asList(this.getMouseMotionListeners())).
                contains(this))
            this.removeMouseMotionListener(this);        
        else
            this.addMouseMotionListener(this);            
    }
    
    /**
     * Starts moving the component to the target location over time.
     * @param target Target location for the component (center of the component).
     * @param time Time (in milliseconds) for the component to move to the target.
     */
    public void moveComponent(Point target, long time)
    {
        timeLeft = time;
        this.target = target;
        this.intial = new Point(follow.getX(), follow.getY());
        totalTime = time;
        componentX = (float)intial.getX();
        componentY = (float)intial.getY();
    }
    
    /**
     * Changes the component being drawn to c.
     * @param c New component to be drawn.
     */
    public void setComponent(Component c)
    {
        this.remove(follow);
        this.follow = c;
    }
    
    /**
     * Removes the component from the panel.
     */
    public void removeComponent()
    {
        this.remove(follow);
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
        long time = System.currentTimeMillis();
        Point draw = location;
        if(target != null)
        {
            long elapsed = time - lastTime;
            
            if(elapsed >= timeLeft)
            {
                timeLeft = 0;
                draw = target;
                target = null;
                location = target;
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
        follow.setBounds(draw.x-size.width/2, draw.y-size.height/2, 
                size.width, size.height);
        lastTime = time;
        super.paintComponent(g);
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
