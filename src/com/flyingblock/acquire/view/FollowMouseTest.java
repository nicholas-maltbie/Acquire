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
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import javax.swing.JFrame;

/**
 * Class meant to test the FollowMouse class.
 * @author Nicholas Maltbie
 */
public class FollowMouseTest implements MouseListener
{
    /**
     * FollowMouse pane.
     */
    private FollowMouse follow; 
   
    public FollowMouseTest(Component c, Dimension dimensions)
    {
        //follow = new FollowMouse(c,dimensions,100);
        follow.pause();
        follow.addMouseListener(this);
        JFrame frame = new JFrame("Follow Mouse Test");
        frame.setBounds(100,100,900,900);
        frame.setContentPane(follow);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        follow.moveComponent(e.getPoint(), 1000l);
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        if(follow.getComponentBounds().contains(e.getPoint()))
            follow.startFolowing();        
    }

    @Override
    public void mouseReleased(MouseEvent e) 
    {
        follow.pause();
    }

    @Override
    public void mouseEntered(MouseEvent e) 
    {
        
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        follow.pause();
    }
        
}
