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
            this.addMouseMotionListener(this);
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
        this.intial = new Point(follow.getX() + (int)size.getWidth(),
                follow.getY() + (int)size.getHeight());
        totalTime = time;
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
        if(target == null)
            follow.setBounds(location.x-size.width/2, location.y-size.height/2, 
                    size.width, size.height);
        else
        {
            long elapsed = time - lastTime;
            
            if(elapsed >= timeLeft)
            {
                follow.setBounds(target.x-size.width/2, target.y-size.height/2,
                        size.width, size.height);
                timeLeft = 0;
                location = target;
                target = null;
                return;
            }
            else
            {
                double percent = elapsed / totalTime;
                System.out.println(totalTime);
                int xDist = (int)(percent * (target.getX() - this.intial.getX()));
                int yDist = (int)(percent * (target.getY() - this.intial.getY()));
                System.out.println(xDist + ", " + yDist);
                follow.setLocation(xDist + follow.getX(), yDist + 
                        follow.getY());
            }
            
            timeLeft -= elapsed;
            
            new java.util.Timer().schedule( 
                new java.util.TimerTask() {
                    @Override
                    public void run() {
                        repaint();
                    }
                }, 
                100);
        }
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
