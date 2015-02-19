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

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.MarketValue;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import javax.swing.JFrame;

/**
 *
 * @author Nick_Pro
 */
public class ViewTest 
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        AcquireBoard board = new AcquireBoard();
        Corporation nicklandia = new Corporation("Nicklandia", board, MarketValue.HIGH, new Color(255, 128, 0));
        Hotel hotel = new Hotel(new Location(1,4), nicklandia);
        frame.setLayout(new GridLayout(1,2));
        frame.add(new HotelView(hotel, Color.WHITE, "COPPER BLACK"));
        frame.add(new HotelView(hotel, Color.WHITE, "COPPER BLACK"));
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
    }
}
