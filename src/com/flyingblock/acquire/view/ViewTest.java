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
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;

/**
 * Class meant for testing the view components.
 * @author Nicholas Maltbie
 */
public class ViewTest implements BoardListener, HandListener, 
        CompanyPanelListener, MouseListener, FollowMouseListener
{
    private static FollowMouse follower;
    
    private static GameView gameView;
    
    private static int[] xPoints = {0,0,900,900,800,800};
    private static int[] yPoints = {0,1300,1300,200,200,0};
    private Point[] points = {new Point(0,0), new Point(0,1300),
        new Point(900,1300), new Point(900,200), new Point(200,800),
        new Point(0,800)};
    
    private static Investor investor;
    private static AcquireBoard board;
    private static Corporation nicklandia;
    
    public static void main(String[] args)
    {
        JFrame frame = new JFrame("Test");
        frame.setBounds(100,100,900,900);
        
        board = new AcquireBoard();
        nicklandia = new Corporation("Nicklandia", board, 
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
        
        investor = new Investor("Clyde", 6000, 6, Color.CYAN);
        
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
        
        /*OpponentsPanel oppsView = new OpponentsPanel(opponents.toArray(new Investor[opponents.size()]), 
                companies, Color.BLACK, "TIMES NEW ROMAN", Font.BOLD,
                new Dimension(100,100), false, new Rectangle(0,0,100,100));
        
        PlayerStatusIcon icon = new PlayerStatusIcon(PlayerStatus.ACTING, investor, "TIMES NEW ROMAN", Font.BOLD, Color.BLACK);
        /*
        Corporation corporation, Font nameFont, 
            int nameStyle, Font stockFont, int stockStyle
        */
        
        HotelView testDrag = new HotelView(hotel, Color.WHITE, "TIMES NEW ROMAN");
        /*AcquireBoard board, List<Corporation> companies,
            Investor investor, List<Investor> opponents, String font*/
        
        gameView = new GameView(board, companies, investor,
                opponents, "TIMES NEW ROMAN", true);
        gameView.setupAndDisplayGUI();
        
        ViewTest viewTest = new ViewTest();
        
        gameView.addBoardListener(viewTest);
        gameView.addHandListener(viewTest);
        gameView.addCompanyPanelListener(viewTest);
        gameView.addMouseListener(viewTest);
        gameView.addFollowMouseListener(viewTest);
        gameView.removeFollowingComponent();
        /*JPanel test = new JPanel();
        
        testDrag.setBounds(0,0,50,50);
        test.add(testDrag, 0);
        follower = new FollowMouse(test, testDrag,
                new Dimension(50,50), 1000);
        follower.startFolowing();
        follower.moveComponent(new Point(500,500), 3000);
        follower.setBounds(new Rectangle(0,0,900,1300));
        
        test.setBackground(Color.BLACK);
        
        JPanel panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill =  GridBagConstraints.BOTH;
        c.weightx = 1.5;
        c.weighty = 1;
        panel.add(playerView, c);
        
        c.gridx = 1;
        c.weightx = 1;
        panel.add(oppsView, c);
        
        c.gridx = 0;
        c.gridy = 1;
        c.weightx = 1.5;
        c.weighty = 3;
        panel.add(boardView, c);
        
        c.gridx = 1;
        c.weightx = 1;
        panel.add(companiesView, c);
        
        test.add(panel);
        panel.setBounds(0,0,1280,860);
        playerView.setupListeners(test);
        boardView.setupListener(test);
        
        /*test.add(playerView);
        playerView.setBounds(0,0,900,200);
        test.add(boardView);
        boardView.setBounds(0,200,900,660);
        test.add(oppsView);
        oppsView.setBounds(900,0,380,200);
        companiesView.setBounds(900,200,380,660);
        
        test.setLayout(null);
        frame.getContentPane().addMouseListener(new ViewTest());
        frame.setBounds(0,0,1300,900);
        frame.setBackground(Color.BLACK);
        frame.setLayout(new BorderLayout());
        frame.add(test, BorderLayout.CENTER);
        frame.setVisible(true);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        companiesView.setPreferredCompanySize(new Dimension(200,100));
        
        boardView.update();
        playerView.update();
        oppsView.update();
        companiesView.update();
        test.validate();
        */
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
        if(held != null)
        {
            board.set(button.getRow(), button.getCol(), held);
            held = null;
            gameView.update();
            gameView.repaint();
            gameView.removeFollowingComponent();
            
            nicklandia.incorporateRegoin();
        }
    }
    
    @Override
    public void handClicked(int index, MouseEvent event) {
        System.out.println("hand clicked " + index);
    }
    
    Hotel held = null;

    @Override
    public void handPressed(int index, MouseEvent event) {
        if(investor.getFromHand(index) != null)
        {
            gameView.moveComponent(event.getPoint());
            held = investor.removeFromHand(index);
            hand = index;
            gameView.setFollowSize(gameView.getHandPieceBounds());
            gameView.setFollowingComponent(new HotelView(held, Color.WHITE, "TIMES NEW ROMAN"));
            gameView.startFollowing();
            gameView.update();
            gameView.repaint();
        }
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
        
    }

    int hand = 0;
    
    @Override
    public void mouseReleased(MouseEvent e)
    {
        if(held != null)
        {
            gameView.moveComponent(gameView.getHandLocation(hand), 500);
        }
    }

    @Override
    public void mouseEntered(MouseEvent e)
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void mouseExited(MouseEvent e) 
    {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void finishedMove(Point target)
    {
        if(held != null)
        {
            investor.setInHand(hand, held);
            gameView.removeFollowingComponent();
            held = null;
            gameView.update();
        }
    }

    @Override
    public void exitedBounds(Point currentLocation) 
    {
        if(held != null)
        {
            gameView.moveComponent(gameView.getHandLocation(hand), 500);
        }
    }
}
