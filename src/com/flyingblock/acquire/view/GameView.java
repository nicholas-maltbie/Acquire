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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseListener;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 * A combination of all view component to form an overall game view.
 * @author Nicholas Maltbie
 */
public class GameView extends JFrame implements ComponentListener
{
    /**
     * Manager to have a component follow the mouse.
     */
    private FollowMouse follower;
    /**
     * Visual representation of a board.
     */
    private BoardView boardView;
    /**
     * Player view for the current player.
     */
    private PlayerView playerView;
    /**
     * Panel to view opponents status.
     */
    private OpponentsPanel oppsView;
    /**
     * View of the companies.
     */
    private CompaniesScrollView companiesView;
    /**
     * The JPanel that holds game elements.
     */
    private JPanel game;
    /**
     * Panel that holds game elements.
     */
    private JPanel panel;
    /**
     * Last bounds of the window.
     */
    private Rectangle lastBounds;
    
    public GameView(AcquireBoard board, List<Corporation> companies,
            Investor investor, List<Investor> opponents, String font,
            boolean displayStocks)
    {
        super("Acquire");
        
        boardView = new BoardView(board, Color.BLACK, Color.WHITE, Color.WHITE, font);
        
        playerView = new PlayerView(investor, companies, font,
                font, font, font
                , Color.BLACK, Color.WHITE, Font.BOLD, Font.ITALIC);
        
        oppsView = new OpponentsPanel(opponents.toArray(new Investor[opponents.size()]), 
                companies, Color.BLACK, font, Font.BOLD, displayStocks);
        
        companiesView = new CompaniesScrollView(companies,
                font, font, Font.ITALIC,
                Color.BLACK);
        
        game = new JPanel();

        follower = new FollowMouse(game, null, new Dimension(100,100), 1000);
        game.setBackground(Color.BLACK);
        
        panel = new JPanel();
        panel.setLayout(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        
        c.fill =  GridBagConstraints.BOTH;
        c.weightx = 2;
        c.weighty = .8;
        panel.add(playerView, c);
        
        c.gridx = 1;
        c.weightx = 1;
        panel.add(oppsView, c);
        
        c.gridx = 0;
        c.gridy = 2;
        c.weightx = 1.5;
        c.weighty = 3;
        panel.add(boardView, c);
        
        c.gridx = 1;
        c.weightx = 1;
        panel.add(companiesView, c);
        
        game.setLayout(null);
        game.add(panel);
        
        panel.setBounds(0,0,1280,860);
        setBounds(0,0,1300,900);
        
        lastBounds = this.getBounds();
    }
    
    /**
     * Sets up and GUI and listeners for the JFrame.
     */
    public void setupAndDisplayGUI()
    {
        playerView.setupListeners(game);
        boardView.setupListener(game);
        
        game.setLayout(null);
        setBackground(Color.BLACK);
        setLayout(new BorderLayout());
        add(game, BorderLayout.CENTER);
        setVisible(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        addComponentListener(this);
        update();
        componentResized(new ComponentEvent(this, 0));
    }
    
    /**
     * Moves the component that is following the mouse.
     * @param point Point to transfer the component to.
     * @param time Time to delay.
     */
    public void moveComponent(Point point, int time)
    {
        follower.moveComponent(point, time);
    }
    
    /**
     * Instantly moves the component that is following the mouse.
     * @param point Point to translate the component to.
     */
    public void moveComponent(Point point)
    {
        follower.moveComponent(point);
    }
    
    /**
     * This resumes or starts the component to follow the mouse.
     */
    public void startFollowing()
    {
        follower.startFolowing();
    }
    
    /**
     * This will stop the component from following the mouse.
     */
    public void stopFollowing()
    {
        follower.pause();
    }
    
    /**
     * Adds a listener to watch the component following the mouse.
     * @param listener Listener to add.
     */
    public void addFollowMouseListener(FollowMouseListener listener)
    {
        follower.addListener(listener);
    }
    
    /**
     * Removes a listener from the follow mouse component.
     * @param listener Listener to remove.
     */
    public void removeFollowMouseListener(FollowMouseListener listener)
    {
        follower.removeListener(listener);
    }
    
    /**
     * Sets the component following the mouse.
     * @param c Component to follow the mouse.
     */
    public void setFollowingComponent(Component c)
    {
        follower.setComponent(c);
        game.add(c, 0);
    }
    
    /**
     * Removes the component following the mouse.
     */
    public void removeFollowingComponent()
    {
        follower.removeComponent();
    }
    
    /**
     * Updates the components being displayed on the screen.
     */
    public final void update()
    {
        boardView.update();
        playerView.update();
        oppsView.update();
        companiesView.update();
        game.validate();
        follower.setBounds(new Rectangle(0,0,boardView.getWidth() + 20, 
                playerView.getHeight() + boardView.getHeight()));
    }

    @Override
    public void componentResized(ComponentEvent e) {
        if(e.getSource() == this)
        {
            companiesView.setPreferredCompanySize(new Dimension(
                    companiesView.getWidth()/10,companiesView.getHeight()/5));
            oppsView.setPreferredCompanySize(new Dimension(
                    oppsView.getWidth()/10,oppsView.getHeight()));
            game.setSize(this.getSize());
            panel.setSize(new Dimension(getWidth()-15, getHeight()-35));
            //System.out.println(this.getSize() + ", " +  game.getSize());
            follower.setBounds(new Rectangle(0,0,boardView.getWidth() + 20, 
                    playerView.getHeight() + boardView.getHeight()));
            lastBounds = this.getBounds();
        }
    }

    @Override
    public void addMouseListener(MouseListener listener)
    {
        game.addMouseListener(listener);
    }
    
    @Override
    public void removeMouseListener(MouseListener listener)
    {
        game.removeMouseListener(listener);
    }
    
    @Override
    public void componentMoved(ComponentEvent e) {
        if(!this.getBounds().equals(lastBounds))
            componentResized(e);
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //do nothing
    }
    
    /**
     * Removes a CompanyPanelListener from the companies panel.
     * @param listener Listener to remove.
     */
    public void removeCompanyPanelListener(CompanyPanelListener listener)
    {
        companiesView.removeButtonListener(listener);
    }
    
    /**
     * Adds a CompanyPanelListener to the companies panel.
     * @param listener Listener to add.
     */
    public void addCompanyPanelListener(CompanyPanelListener listener)
    {
        companiesView.addButtonListener(listener);
    }
    
    /**
     * Adds a hand listener to the playerView.
     * @param listener Listener to add.
     */
    public void addHandListener(HandListener listener)
    {
        playerView.addHandLIstener(listener);
    }
    
    /**
     * Removes a hand listener from the playerView.
     * @param listener Listener to remove.
     */
    public void removeHandListener(HandListener listener)
    {
        playerView.removeHandListener(listener);
    }
    
    /**
     * Adds a listener to the boardView. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to add.
     */
    public void addBoardListener(BoardListener listener)
    {
        boardView.addBoardListener(listener);
    }
    
    /**
     * Removes a listener from the board. Board listeners listen to mouse
     * actions that are located within the board.
     * @param listener Listener to remove.
     * @return If the listener was successfully removed from the list.
     */
    public boolean removeBoardListener(BoardListener listener)
    {
        return boardView.removeBoardListener(listener);
    }
    
    /**
     * Gets the location of a hand piece in the GUI.
     * @param index Index in the hand.
     * @return Returns a point that is the upper left corner of the hand location.
     */
    public Point getHandLocation(int index)
    {
        return playerView.getHandLocation(index);
    }
    
    /**
     * Gets the size of any piece within the player's hand.
     * @return Returns the width and height of any piece in the player's hand.
     */
    public Dimension getHandPieceBounds()
    {
        return playerView.getPieceBounds();
    }
    
    /**
     * Gets the size of any piece within the board.
     * @return Returns the width and height of any hotel view within the board.
     */
    public Dimension getBoardPieceBounds()
    {
        return boardView.getPieceBounds();
    }
    
    /**
     * Sets the size of the component following the cursor.
     * @param size Dimensions to size the component following the cursor
     * in pixels.
     */
    public void setFollowSize(Dimension size)
    {
        follower.setFollowSize(size);
    }
}
