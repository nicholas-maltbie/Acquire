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
    
    public FollowMouse(Component follow, Dimension size)
    {
        this.follow = follow;
        this.size = size;
        follow.setPreferredSize(size);
        this.addMouseMotionListener(this);
        location = new Point(0,0);
        this.add(follow);
    }
    
    public void setFollowSize(Dimension size)
    {
        this.size = size;
        follow.setPreferredSize(size);
    }
    
    @Override
    public void paintComponent(Graphics g)
    {
        super.paintComponent(g);
        follow.setBounds(location.x, location.y, size.width, size.height);
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
