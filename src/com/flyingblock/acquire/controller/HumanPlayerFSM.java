/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.controller;

import com.flyingblock.acquire.controller.HumanPlayerFSM.TurnState;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.HotelMarket;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.view.BuyStockPanel;
import com.flyingblock.acquire.view.BuyStockPanelListener;
import com.flyingblock.acquire.view.GameView;
import java.awt.Color;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This is a State Machine that will run a human player's turn allowing him/her 
 * to edit the board according to the rules of the game.
 * @author Nicholas Maltbie
 */
public class HumanPlayerFSM extends AbstractFSM<TurnState> implements PlayerListener,
        BuyStockPanelListener
{
    /**
     * Game board.
     */
    private AcquireBoard board;
    /**
     * Deck to draw tiles from.
     */
    private HotelMarket deck;
    /**
     * Current player that is taking his/her turn.
     */
    private Investor player;
    /**
     * Corporations that have a presence in the game.
     */
    private List<Corporation> companies;
    /**
     * View with all the buttons and fun stuff :D.
     */
    private GameView view;
    /**
     * Piece manager that allows players to move pieces around the board.
     */
    private PieceManager manager;
    /**
     * Location of interest for the player.
     */
    private Location locationOfInterest;
    /**
     * machine Machine that is running the game.
     */
    private AcquireMachine machine;
    
    /**
     * Constructs a human player's turn. Can be started and will take over the
     * GUI and operate the turn.
     * @param machine Machine that is running the game.
     * @param board Game board.
     * @param deck Game deck.
     * @param player Player who is taking his/her turn.
     * @param companies Companies.
     * @param view The GUI display that has all the panels.
     */
    public HumanPlayerFSM(AcquireMachine machine, AcquireBoard board, HotelMarket deck, 
            Investor player, List<Corporation> companies, GameView view)
    {
        super(stateMap, TurnState.PLACE_PIECE);
        this.machine = machine;
        this.board = board;
        this.deck = deck;
        this.player = player;
        this.companies = companies;
        this.view = view;
        manager = new PieceManager(view, player, board);
    }
    
    @Override
    public void start()
    {
        manager.start();
        manager.addPlayerListener(this);
        manager.setReturnToHand(false);
        view.setDrag(true);
    }

    @Override
    protected void stateStarted(TurnState state) 
    {
        switch(state)
        {
            case PLACE_PIECE:
                break;
            case CREATE_COMPANY:
                List<Corporation> taken = board.getCompaniesOnBoard();
                List<Corporation> available = new ArrayList<>();
                for(Corporation c : companies)
                    if(!taken.contains(c))
                        available.add(c);
                String[] names = new String[available.size()];
                for(int i = 0; i < available.size(); i++)
                    names[i] = available.get(i).getCorporateName();
                JFrame compnayChoicePane = new JFrame();
                String chosen = null;
                do {
                    chosen = (String)JOptionPane.showInputDialog(
                                        compnayChoicePane,
                                        "Chose a company to create",
                                        "new company",
                                        JOptionPane.QUESTION_MESSAGE,
                                        null,
                                        names,
                                        names[0]);
                } while(chosen == null);
                Corporation chosenCompany = null;
                for(int i = 0; i < available.size(); i++)
                    if(chosen.equals(available.get(i).getCorporateName()))
                        chosenCompany = available.get(i);
                if(chosenCompany != null)
                    chosenCompany.setHeadquarters(locationOfInterest);
                else
                    throw new IllegalStateException("Cannot extablish a null corporation");
                chosenCompany.incorporateRegoin();
                player.addStock(chosenCompany.getStock());
                player.addStock(chosenCompany.getStock());
                
                view.update();
                view.repaint();
                
                setState(TurnState.BUY_STOCKS);
                break;
            case BUY_STOCKS:
                manager.start();
                manager.disallowBoardPlacement();
                view.setDrag(true);
                for(Corporation c : board.getCompaniesOnBoard())
                {
                    c.incorporateRegoin();
                }
                view.update();
                view.repaint();
                BuyStockPanel panel = new BuyStockPanel(companies.toArray(new Corporation[companies.size()]),
                    player, 3, "TIMES NEW ROMAN", "TIMES NEW ROMAN", Color.BLACK);
                panel.addListener(this);
                panel.setupAndDisplayGUI();
                break;
            case END_TURN:
                manager.stop();
                player.drawFromDeck(deck);
                view.update();
                view.repaint();
                machine.turnEnded(player);
                break;
        }
    }

    @Override
    protected void stateEnded(TurnState state) 
    {
        switch(state)
        {
            case PLACE_PIECE:
                manager.stop();
                break;
        }
    }

    @Override
    public void piecesSwapped(Hotel move1, int index1, Hotel move2, int index2)
    {
        
    }

    @Override
    public void piecePlaced(Hotel placed, Location loc)
    {
        //Decide if the move is valid
        //check if the piece connects 2+ safe corporations
        int numSave = 0;
        List<Corporation> accessories = board.getCorporationsInBlob(
                loc.getRow(), loc.getCol());
        for(Corporation company : accessories)
        {
            //a company is save if it has 11+ hotels in its hold
            if(company.getNumberOfHotels() >= 11)
                numSave++;
        }
        //check if the played location lines up with the hotel's location
        boolean isValid = placed.getLocation().equals(loc) && numSave < 2;
        
        if(!isValid) {
            player.addPieceToHand(board.remove(loc.getRow(), loc.getCol()));
            return;
        }
        
        final TurnState nextState;
        
        //Choose if a merger happens.
        boolean merger = numSave == 1 && accessories.size() > 1;
        List<Corporation> taken = board.getCompaniesOnBoard();
        List<Corporation> available = new ArrayList<>();
        for(Corporation c : companies)
            if(!taken.contains(c))
                available.add(c);
        //Chose if a new company is formed.
        List<Location> blob = board.getBlob(loc.getRow(), loc.getCol());
        boolean formCompany = available.size() > 0 && blob.size() >= 2;
        for(Location l : blob)
            if(board.getCorporation(l.getRow(), l.getCol()) != null)
                formCompany = false;
        if(merger)
            nextState = TurnState.MERGER;
        else if(formCompany)
            nextState = TurnState.CREATE_COMPANY;
        else
            nextState = TurnState.BUY_STOCKS;
        
        locationOfInterest = loc;
        
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    setState(nextState);
                }
            }, 0);
    }

    @Override
    public void buyingComplete(int[] stocksBought, Corporation[] companies) 
    {
        view.update();
        view.repaint();
        setState(TurnState.END_TURN);
    }
    
    /**
     * Turn states.
     */
    public static enum TurnState {PLACE_PIECE, MERGER, CREATE_COMPANY, 
            BUY_STOCKS, END_TURN};
    /**
     * State map for the turn states.
     */
    public static final HashMap<TurnState, EnumSet<TurnState>> stateMap;
    /**
     * setups turn states.
     */
    static {
        stateMap = new HashMap<>();
        stateMap.put(TurnState.PLACE_PIECE, EnumSet.of(TurnState.MERGER, 
                TurnState.BUY_STOCKS, TurnState.CREATE_COMPANY));
        stateMap.put(TurnState.MERGER, EnumSet.of(TurnState.BUY_STOCKS));
        stateMap.put(TurnState.CREATE_COMPANY, EnumSet.of(TurnState.BUY_STOCKS));
        stateMap.put(TurnState.BUY_STOCKS, EnumSet.of(TurnState.END_TURN));
        stateMap.put(TurnState.END_TURN, EnumSet.noneOf(TurnState.class));
    }
}
