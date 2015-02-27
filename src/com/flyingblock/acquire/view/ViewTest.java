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
import com.flyingblock.acquire.model.Stock;
import com.flyingblock.acquire.view.PlayerStatusIcon.PlayerStatus;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

/**
 * Class meant for testing the view components.
 * @author Nicholas Maltbie
 */
public class ViewTest implements BoardListener, HandListener, CompanyPanelListener, MouseListener
{
    private static FollowMouse follower;
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        
        AcquireBoard board = new AcquireBoard();
        Corporation nicklandia = new Corporation("Nicklandia", board, 
                MarketValue.HIGH, new Color(255, 128, 0), 10);
        nicklandia.setHeadquarters(new Location(1,1));
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
                investor.setInHand(i, new Hotel(new Location(0,i)));
        }
        
        Hotel hotel = new Hotel(new Location(1,2), nicklandia);
        frame.setLayout(new GridLayout(1,2));
        board.set(1,2, hotel);
        board.set(1,1, new Hotel(new Location(1,1)));
        board.set(1,11, new Hotel(new Location(1,11)));
        BoardView boardView = new BoardView(board, Color.BLACK, Color.WHITE, Color.WHITE, "TIMES NEW ROMAN");
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
                , Color.BLACK, Color.WHITE, Font.BOLD, Font.ITALIC);
        playerView.addHandLIstener(new ViewTest());
        Investor o1 = new Investor("CPU1", 6000, 6, Color.CYAN);
        Investor o2 = new Investor("Brian", 6000, 6, Color.RED);
        Investor o3 = new Investor("Hue", 6000, 6, Color.BLUE);
        Investor o4 = new Investor("G", 6000, 6, Color.GREEN);
        
        List<Investor> opponents = new ArrayList<>(Arrays.asList(o1,o2,o3,o4));
        
        OpponentsPanel oppsView = new OpponentsPanel(opponents.toArray(new Investor[opponents.size()]), 
                companies, Color.BLACK, "TIMES NEW ROMAN", Font.BOLD,
                new Dimension(100,100), false, new Rectangle(0,0,100,100));
        
        PlayerStatusIcon icon = new PlayerStatusIcon(PlayerStatus.ACTING, investor, "TIMES NEW ROMAN", Font.BOLD, Color.BLACK);
        /*
        Corporation corporation, Font nameFont, 
            int nameStyle, Font stockFont, int stockStyle
        */
        CorporationView companyView = new CorporationView(nicklandia, 
                "TIMES NEW ROMAN", Font.BOLD, "TIMES NEW ROMAN", Font.ITALIC, 
                Color.BLACK);
        CompaniesScrollView companiesView = new CompaniesScrollView(companies,
                "TIMES NEW ROMAN", Font.BOLD, "TIMES NEW ROMAN", Font.ITALIC,
                Color.BLACK, new Dimension(400,100));
        companiesView.addButtonListener(new ViewTest());
        
        JPanel test = new JPanel();
        
        test.add(playerView);
        playerView.setBounds(0,0,500,100);
        playerView.setupListeners(test);
        test.add(boardView);
        boardView.setBounds(0,100,500,500);
        HotelView testDrag = new HotelView(hotel, Color.WHITE, "TIMES NEW ROMAN");
        testDrag.setBounds(0,0,50,50);
        test.add(testDrag, 0);
        follower = new FollowMouse(test, testDrag,
                new Dimension(50,50), 100);
        follower.startFolowing();
        follower.moveComponent(new Point(500,500), 3000);
        
        test.setLayout(null);
        frame.getContentPane().addMouseListener(new ViewTest());
        frame.setBounds(0,0,1300,900);
        frame.setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());
        frame.add(test, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
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
        //System.out.println("entered");
    }

    @Override
    public void exitBoard(MouseEvent event) {
        //System.out.println("exited");
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

    @Override
    public void buyButtonPressed(Corporation company) {
        System.out.println(company);
    }

    @Override
    public void mouseClicked(MouseEvent e)
    {
        
    }

    @Override
    public void mousePressed(MouseEvent e)
    {
        follower.startFolowing();
    }

    @Override
    public void mouseReleased(MouseEvent e)
    {
        follower.pause();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
