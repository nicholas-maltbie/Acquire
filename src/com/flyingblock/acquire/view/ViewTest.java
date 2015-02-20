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
import java.awt.GridLayout;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;

/**
 *
 * @author Nick_Pro
 */
public class ViewTest implements BoardListener
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        AcquireBoard board = new AcquireBoard();
        Corporation nicklandia = new Corporation("Nicklandia", board, MarketValue.HIGH, new Color(255, 128, 0));
        Hotel hotel = new Hotel(new Location(1,2), nicklandia);
        frame.setLayout(new GridLayout(1,2));
        board.set(1,2, hotel);
        board.set(1,1, new Hotel(new Location(1,1)));
        board.set(1,11, new Hotel(new Location(1,11)));
        BoardView view = new BoardView(board, Color.BLACK, Color.WHITE, Color.BLUE, "TIMES NEW ROMAN");
        frame.add(view);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        view.addBoardListener(new ViewTest());        
    }

    @Override
    public void buttonClicked(Location button, MouseEvent event) {
        System.out.println("clicked " + button);
    }

    @Override
    public void buttonPressed(Location button, MouseEvent event) {
        System.out.println("pressed " + button);
    }

    @Override
    public void buttonRelease(Location button, MouseEvent event) {
        System.out.println("released " + button);
    }

    @Override
    public void enterBoard(MouseEvent event) {
        System.out.println("entered");
    }

    @Override
    public void exitBoard(MouseEvent event) {
        System.out.println("exited");
    }
}
