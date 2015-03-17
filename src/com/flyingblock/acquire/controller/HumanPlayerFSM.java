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
import com.flyingblock.acquire.view.GameView;
import com.flyingblock.acquire.view.HotelView;
import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This is a State Machine that will run a human player's turn allowing him/her 
 * to edit the board according to the rules of the game.
 * @author Nicholas Maltbie
 */
public class HumanPlayerFSM extends AbstractFSM<TurnState> implements PlayerListener,
        BuyStockPanelListener, SelectGroupListener
{
    /**
     * The size that a corporation needs to be in order to be safe.
     */
    public static final int SAFE_CORPORATION_SIZE = 11;
    
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
                Corporation chosenCompany =  this.choseCorporationFromList(
                        available, "chose a corporation to create", "New Company");
                if(chosenCompany != null)
                    chosenCompany.setHeadquarters(locationOfInterest);
                else
                    throw new IllegalStateException("Cannot extablish a null corporation");
                chosenCompany.incorporateRegoin();
                if(chosenCompany.getNumberOfHotels() > 0)
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
                    c.incorporateRegoin();
                view.update();
                view.repaint();
                BuyStockPanel panel = new BuyStockPanel(companies.toArray(new Corporation[companies.size()]),
                    player, 3, "TIMES NEW ROMAN", "TIMES NEW ROMAN", Color.BLACK);
                panel.addListener(this);
                panel.setupAndDisplayGUI();
                break;
            case END_TURN:
                //remove un-playable tiles
                List<HotelView> hotels = new ArrayList<>();
                for(int i = 0; i < player.getHandSize(); i++)
                {
                    if(player.getFromHand(i) == null)
                        continue;
                    if(canPieceBePlayed(player.getFromHand(i)))
                        hotels.add(new HotelView(player.getFromHand(i), Color.BLACK, "TIMES NEW ROMAN"));
                }
                if(!hotels.isEmpty())
                {
                    SelectGroupPanel selectPanel = new SelectGroupPanel(hotels.toArray(
                            new HotelView[hotels.size()]), "<html>You cannot play these tiles,<br>"
                            + " do you want to keep any?</html>", "KEEP", "REPLACE");
                    selectPanel.setupAndDisplayGUI(new Rectangle(100,100,400+150*(hotels.size()-1),700));
                    selectPanel.addListener(this);
                }
                else
                    finished(new Component[0], new boolean[0]);
                view.update();
                view.repaint();
                //draw new tiles
                break;
            case MERGER:
                List<Corporation> suspects = new ArrayList<>();
                for(Location l : board.getBlob(locationOfInterest.getRow(), 
                        locationOfInterest.getCol()))
                    if(board.isIncorporated(l.getRow(), l.getCol()) && 
                            !suspects.contains(board.getCorporation(l.getRow(), l.getCol())))
                        suspects.add(board.getCorporation(l.getRow(), l.getCol()));
                List<Corporation> largest = new ArrayList<>();
                for(Corporation suspect : suspects)
                {
                    if(largest.isEmpty())
                        largest.add(suspect);
                    else if(suspect.getNumberOfHotels() > largest.get(0).getNumberOfHotels())
                    {
                        largest.clear();
                        largest.add(suspect);
                    }
                    else if(suspect.getNumberOfHotels() == largest.get(0).getNumberOfHotels())
                        largest.add(suspect);
                }
                
                Corporation parent = largest.get(0);
                if(largest.size() > 1)
                {
                    parent =  this.choseCorporationFromList(
                            largest, "chose a corporation to win the merger", "Merger Resolution");
                }
                suspects.remove(parent);
                machine.handelMerger(parent, suspects);
                break;
        }
    }
    
    /**
     * Checks if a piece can be played. A piece would not be able to be played
     * it connected two or more safe corporations.
     * @param hotel Hotel to check.
     * @return Returns if the piece can be played.
     */
    public boolean canPieceBePlayed(Hotel hotel)
    {
        Location loc = hotel.getLocation();
        board.set(loc.getRow(), loc.getCol(), hotel);
        int numSafe = 0;
        for(Corporation c : board.getCorporationsInBlob(loc.getRow(), loc.getCol()))
            if(c.getNumberOfHotels() >= SAFE_CORPORATION_SIZE)
               numSafe++;
        
        board.remove(loc.getRow(), loc.getCol());
        return numSafe >= 2;
    }

    /**
     * Prompts the player to chose a company from a list of corporations.
     * @param options Corporation to choose from.
     * @param text Text to put in window.
     * @param title Title text for the window.
     * @return Returns the corporation that the player chose.
     */
    public Corporation choseCorporationFromList(List<Corporation> options,
            String text, String title)
    {
        String[] names = new String[options.size()];
        for(int i = 0; i < options.size(); i++)
            names[i] = options.get(i).getCorporateName();
        JFrame compnayChoicePane = new JFrame();
        String chosen = null;
        do {
            chosen = (String)JOptionPane.showInputDialog(
                                compnayChoicePane,
                                text,
                                title,
                                JOptionPane.QUESTION_MESSAGE,
                                null,
                                names,
                                names[0]);
        } while(chosen == null);
        Corporation chosenCompany = null;
        for(int i = 0; i < options.size(); i++)
            if(chosen.equals(options.get(i).getCorporateName()))
                chosenCompany = options.get(i);
        return chosenCompany;
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
            if(company.getNumberOfHotels() >= SAFE_CORPORATION_SIZE)
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
        boolean merger = accessories.size() > 1;
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
    
    public void mergerComplete()
    {
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    setState(TurnState.BUY_STOCKS);
                }
            }, 0);
    }

    @Override
    public void finished(Component[] options, boolean[] states) 
    {
        for(int i = 0; i < options.length; i++)
        {
            if(!states[i])
            {
                for(int h = 0; h < player.getHandSize(); h++)
                    if(player.getFromHand(h) != null && player.getFromHand(h).equals(((HotelView)options[i]).getHotel()))
                        player.removeFromHand(h);
            }
        }
        player.drawFromDeck(deck);
        view.update();
        view.repaint();
        manager.stop();
        machine.turnEnded(player);
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
