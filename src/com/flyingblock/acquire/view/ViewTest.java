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
import com.flyingblock.acquire.view.PlayerStatusIcon.PlayerStatus;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Nicholas Maltbie
 */
public class ViewTest implements BoardListener, HandListener
{
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        
        AcquireBoard board = new AcquireBoard();
        Corporation nicklandia = new Corporation("Nicklandia", board, 
                MarketValue.HIGH, new Color(255, 128, 0), 10);
        Corporation tower = new Corporation("Tower", board, 
                MarketValue.LOW, new Color(144, 106, 40), 10);
        Corporation american = new Corporation("MERICA\'", board, 
                MarketValue.MEDIUM, new Color(110, 242, 242), 10);
        Corporation red = new Corporation("Red", board, 
                MarketValue.MEDIUM, Color.RED, 10);
        Corporation analantis = new Corporation("Analantis", board,
                MarketValue.LOW, Color.GREEN.brighter(), 10);
        Corporation imperial = new Corporation("Imperial", board, 
                MarketValue.HIGH, Color.YELLOW, 10);
        Corporation contiental = new Corporation("Contiental", board,
                MarketValue.MEDIUM, Color.MAGENTA, 10);
        
        
        ArrayList<Corporation> companies = new ArrayList<>(
                Arrays.asList(nicklandia, tower, american, red, analantis,
                        imperial, contiental));
        
        Investor investor = new Investor("VeryVeryVeryLongName", 6000, 6, Color.CYAN);
        
        investor.addStock(nicklandia.getStock());
        investor.addStock(nicklandia.getStock());
        investor.addStock(analantis.getStock());
        investor.addStock(analantis.getStock());
        
        for(int i = 0; i < investor.getHandSize(); i++)
        {
            if(i == 1 || i == 2 || i == 4)
                investor.setInHand(i, new Hotel(new Location(0,i), nicklandia));
        }
        
        Hotel hotel = new Hotel(new Location(1,2), nicklandia);
        frame.setLayout(new GridLayout(1,2));
        board.set(1,2, hotel);
        board.set(1,1, new Hotel(new Location(1,1)));
        board.set(1,11, new Hotel(new Location(1,11)));
        BoardView boardView = new BoardView(board, Color.BLACK, Color.WHITE, Color.BLUE, "TIMES NEW ROMAN");
        boardView.addBoardListener(new ViewTest());
        
        OtherPlayerView view = new OtherPlayerView(investor, companies, "TIMES NEW ROMAN",
                "TIMES NEW ROMAN", "TIMES NEW ROMAN", Color.BLACK, Font.BOLD, Font.ITALIC, true);
        //view.addHandLIstener(new ViewTest());
        
        /*
        Investor player, List<Corporation> corporatoins, String stockFont,
            String nameFont, String moneyFont, String handFont, Color background,
            Color gridColor, int nameStyle, int moneyStyle
        */
        PlayerView playerView = new PlayerView(investor, companies, "TIMES NEW ROMAN",
                "TIMES NEW ROMAN", "TIMES NEW ROMAN", "TIMES NEW ROMAN"
                , Color.BLACK, Color.BLUE, Font.BOLD, Font.ITALIC);
        
        Investor o1 = new Investor("CPU1", 6000, 6, Color.CYAN);
        Investor o2 = new Investor("Brian", 6000, 6, Color.RED);
        Investor o3 = new Investor("Hue", 6000, 6, Color.BLUE);
        Investor o4 = new Investor("GreenPlayer", 6000, 6, Color.GREEN);
        Investor o5 = new Investor("PurplePeeps", 6000, 6, Color.MAGENTA);
        
        List<Investor> opponents = new ArrayList<>(Arrays.asList(o1,o2,o3,o4,o5));
        
        OpponentsPanel oppsView = new OpponentsPanel(opponents.toArray(new Investor[opponents.size()]), 
                companies, Color.BLACK, "TIMES NEW ROMAN", Font.BOLD,
                new Dimension(100,400), true, new Rectangle(0,0,100,100));
        
        PlayerStatusIcon icon = new PlayerStatusIcon(PlayerStatus.ACTING, investor, "TIMES NEW ROMAN", Font.BOLD, Color.BLACK);
        
        frame.add(playerView);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    o1.addStock(imperial.getStock());
                    o1.addStock(imperial.getStock());
                    o1.addStock(imperial.getStock());
                    o1.addStock(imperial.getStock());
                    oppsView.repaint();
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

    @Override
    public void handClicked(int index, MouseEvent event) {
        System.out.println("hand clicked " + index);
    }

    @Override
    public void handPressed(int index, MouseEvent event) {
        System.out.println("hand pressed " + index);
    }

    @Override
    public void handReleased(int index, MouseEvent event) {
        System.out.println("hand released " + index);
    }
}
