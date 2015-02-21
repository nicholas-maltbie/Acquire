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
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.MarketValue;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.JFrame;

/**
 *
 * @author Nicholas Maltbie
 */
public class ViewTest implements BoardListener
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        
        AcquireBoard board = new AcquireBoard();
        Corporation nicklandia = new Corporation("Nicklandia", board, 
                MarketValue.HIGH, new Color(255, 128, 0));
        Corporation tower = new Corporation("Tower", board, 
                MarketValue.LOW, new Color(144, 106, 40));
        Corporation american = new Corporation("MERICA\'", board, 
                MarketValue.MEDIUM, new Color(110, 242, 242));
        Corporation red = new Corporation("Red", board, 
                MarketValue.MEDIUM, Color.RED);
        Corporation analantis = new Corporation("Analantis", board,
                MarketValue.LOW, Color.GREEN.brighter());
        
        ArrayList<Corporation> companies = new ArrayList<>(
                Arrays.asList(nicklandia, tower, american, red, analantis));
        
        Investor investor = new Investor("Nick", 6000, 6);
        
        investor.addStock(nicklandia.getStock());
        investor.addStock(nicklandia.getStock());
        investor.addStock(analantis.getStock());
        investor.addStock(analantis.getStock());
        
        for(int i = 0; i < investor.getHandSize(); i++)
        {
            if(i == 1 || i == 2 || i == 4)
                investor.setInHand(i, new Hotel(new Location(0,i), nicklandia));
        }
        
        /*
        Hotel hotel = new Hotel(new Location(1,2), nicklandia);
        frame.setLayout(new GridLayout(1,2));
        board.set(1,2, hotel);
        board.set(1,1, new Hotel(new Location(1,1)));
        board.set(1,11, new Hotel(new Location(1,11)));
        BoardView view = new BoardView(board, Color.BLACK, Color.WHITE, Color.BLUE, "TIMES NEW ROMAN");
        frame.add(view);
        view.addBoardListener(new ViewTest());*/
        
        /*
        Investor player, List<Corporation> corporatoins, String stockFont,
            String nameFont, String moneyFont, String handFont, Color background,
            Color gridColor)
        */
        
        PlayerView view = new PlayerView(investor, companies, "TIMES NEW ROMAN",
                "TIMES NEW ROMAN", "TIMES NEW ROMAN", "TIMES NEW ROMAN", 
                Color.BLACK, Color.BLUE, Color.MAGENTA, Color.GREEN.brighter(),
                Font.BOLD, Font.ITALIC);
        
        frame.add(view);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    investor.removeFromHand(2);
                    view.update();
                }
            }, 
            5000 
        );
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
