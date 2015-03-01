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
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.util.ArrayList;
import java.util.Arrays;
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
                font, Font.BOLD, font, Font.ITALIC,
                Color.BLACK);
        
        game = new JPanel();
        
        follower = new FollowMouse(game, null, new Dimension(50,50), 1000);
        game.setBackground(Color.BLACK);
        
        panel = new JPanel();
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
        
        game.setLayout(new BorderLayout());
        game.add(panel);
        panel.setBounds(0,0,1280,860);
        setBounds(0,0,1300,900);
    }
    
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
    
    public void addFollowMouseListener(FollowMouseListener listener)
    {
        follower.addListener(listener);
    }
    
    public void setFollowingComponent(Component c)
    {
        follower.setComponent(c);
    }
    
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
            follower.setBounds(new Rectangle(0,0,playerView.getWidth(), 
                    playerView.getHeight() + boardView.getHeight()));
            this.update();
        }
    }

    @Override
    public void componentMoved(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentShown(ComponentEvent e) {
        //do nothing
    }

    @Override
    public void componentHidden(ComponentEvent e) {
        //do nothing
    }
}
