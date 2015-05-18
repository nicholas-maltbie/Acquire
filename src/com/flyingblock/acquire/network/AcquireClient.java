/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.network;

import com.flyingblock.acquire.controller.AcquireRules;
import com.flyingblock.acquire.controller.BuyStockPanel;
import com.flyingblock.acquire.controller.BuyStockPanelListener;
import com.flyingblock.acquire.controller.HumanPlayerFSM;
import com.flyingblock.acquire.controller.MergerPanel;
import com.flyingblock.acquire.controller.MergerPanelListener;
import com.flyingblock.acquire.controller.PieceManager;
import com.flyingblock.acquire.controller.PlayerListener;
import com.flyingblock.acquire.controller.SelectGroupListener;
import com.flyingblock.acquire.controller.SelectGroupPanel;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Board;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.Stock;
import com.flyingblock.acquire.view.GameView;
import com.flyingblock.acquire.view.HotelView;

import java.awt.Color;
import java.awt.Component;
import java.awt.Rectangle;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.Box;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * A client that work with an acquire host and react to events sent from the host.
 * @author Nicholas Maltbie
 */
public class AcquireClient implements ClientListener, PlayerListener,
        BuyStockPanelListener, MergerPanelListener, SelectGroupListener
{
    /**Connection to the server*/
    private Client client;
    /**Game board*/
    private AcquireBoard board;
    /**Game players*/
    private List<Investor> investors;
    /**Companies connected to the server*/
    private List<Corporation> companies;
    /**GameView to help the player*/
    private GameView view;
    /**Current player*/
    private Investor player;
    /**Manages board actions*/
    private PieceManager manager;
    /**GUI for buying stocks*/
    private BuyStockPanel panel;
    /**
     * Creates an acquire client from a client that is already connected to a server
     * @param client Client that is connected to the server.
     * @param board Game Board, provided by the server.
     * @param investors All the game players
     * @param companies Companies participating in the game.
     * @param player Investor that is the player.
     */
    public AcquireClient(Client client, AcquireBoard board, List<Investor> investors, 
            List<Corporation> companies, Investor player)
    {
        this.client = client;
        this.board = board;
        this.investors = investors;
        this.companies = companies;
        this.player = player;
        this.view = new GameView(board, companies, player, investors,
                "TIMES NEW ROMAN", true);
    }
    
    /**
     * Gets the game view that this game is playing on.
     * @return GameView of the player's game.
     */
    public GameView getGameView()
    {
        return view;
    }
    
    /**
     * Initializes the game view for the player.
     */
    public void initView()
    {
        view.setupAndDisplayGUI();
        manager = new PieceManager(view, player, board);
        manager.start();
        manager.disallowBoardPlacement();
        manager.addPlayerListener(this);
    }

    @Override
    public void objectRecieved(Object object)
    {
        if(object instanceof GameEvent)
        {
            GameEvent event = (GameEvent) object;
            EventType type = EventType.identifyEvent(event);
            //possibly a check to see if it's a duplicate event or if it's invalid.
            parseEvent(event, type);
        }
    }

    @Override
    public void disconnectedFromServer() {
        
    }
    
    public void parseEvent(GameEvent event, EventType type)
    {
        //System.out.println("Recieved a " + type + " message");
        switch(type)
        {
            case BOARD_UPDATE:
                AcquireBoard update = (AcquireBoard) event.getMessage();
                for(int r = 0; r < board.getNumRows(); r++)
                    for(int c = 0; c < board.getNumCols(); c++)
                        board.set(r, c, update.get(r, c));
                view.update();
                //System.out.println(update);
                break;
            case PLAYERS_UPDATE:
                Investor[] playerUpdate = (Investor[]) event.getMessage();
                List<Investor> investors = new ArrayList<>(this.investors);
                investors.add(player);
                for(Investor player : playerUpdate)
                {
                    for(int i = 0; i < investors.size(); i++)
                    {
                        if(player.getName().equals(investors.get(i).getName()))
                        {
                            Investor edit = investors.get(i);
                            edit.addMoney(player.getMoney() - edit.getMoney());
                            edit.clearStocks();
                            edit.addStocks(player.getStocks());
                            
                            Hotel[] myHand = new Hotel[edit.getHandSize()];
                            Hotel[] serverHand = new Hotel[edit.getHandSize()];
                            for(int j = 0; j < edit.getHandSize(); j++)
                            {
                                myHand[j] = edit.getFromHand(j);
                                serverHand[j] = player.getFromHand(j);
                            }
                            Hotel[] newHand = new Hotel[edit.getHandSize()];
                            for(int index = 0; index < edit.getHandSize(); index++)
                            {
                                Hotel check = myHand[index];
                                if(check != null)
                                {
                                    for(int serverIndex = 0; serverIndex < edit.getHandSize(); serverIndex++)
                                    {
                                        Hotel serverCheck = serverHand[serverIndex];
                                        if(serverCheck != null && serverCheck.equals(check))
                                        {
                                            newHand[index] = check;
                                        }
                                    }
                                }
                            }
                            for(int serverIndex = 0; serverIndex < edit.getHandSize(); serverIndex++)
                            {
                                Hotel serverAdd = serverHand[serverIndex];
                                boolean add = true;
                                if(serverAdd != null)
                                {
                                    for(int index = 0; index < edit.getHandSize(); index++)
                                    {
                                        Hotel check = myHand[index];
                                        if(check != null && check.equals(serverAdd))
                                        {
                                            add = false;
                                        }
                                    }
                                }
                                if(add)
                                {
                                    boolean hold = true;
                                    for(int index = 0; index < edit.getHandSize(); index++)
                                    {
                                        if(newHand[index] == null && hold)
                                        {
                                            newHand[index] = serverAdd;
                                            hold = false;
                                        }
                                    }
                                }
                            }
                            for(int index = 0; index < edit.getHandSize(); index++)
                            {
                                edit.setInHand(index, newHand[index]);
                            }
                        }
                    }
                }
                for(int i = 0; i < player.getHandSize(); i++)
                {
                    if(player.getFromHand(i) != null && manager.isHoldingPiece() 
                            && manager.getHeld().equals(player.getFromHand(i)))
                    {
                        player.removeFromHand(i);
                    }
                }
                //System.out.println(player.getStocks());
                break;
            case CORPORATIONS_UPDATE:
                Corporation[] corps = (Corporation[]) event.getMessage();
                for(Corporation c : companies)
                {
                    for(int i = 0; i < corps.length; i++)
                    {
                        if(c.getCorporateName().equals(corps[i].getCorporateName()))
                        {
                            c.setAvailableStocks(corps[i].getAvailableStocks());
                            c.setHeadquarters(corps[i].getHeadquarters());
                            if(c.isEstablished())
                                c.incorporateRegoin();
                        }
                    }
                }
                break;
            case BUY_STOCKS:
                manager.start();
                manager.disallowBoardPlacement();
                view.setDrag(true);
                for(Corporation c : board.getCompaniesOnBoard())
                    c.incorporateRegoin();
                view.update();
                view.repaint();
                
                List<Corporation> established = board.getCompaniesOnBoard();
                int lowestPrice = 100000000;
                if(!established.isEmpty())
                    lowestPrice = established.get(0).getStockPrice();
                for(Corporation e : established)
                    if(e.getStockPrice() < lowestPrice)
                        lowestPrice = e.getStockPrice();
                boolean canBuy = player.getMoney() >= lowestPrice && !established.isEmpty();
                sendStocks = true;
                if(canBuy)
                {
                    panel = new BuyStockPanel(companies.toArray(new Corporation[companies.size()]),
                        player, 3, "TIMES NEW ROMAN", "TIMES NEW ROMAN", Color.BLACK);
                    panel.addListener(this);
                    panel.setupAndDisplayGUI();
                }
                else
                {
                    this.buyingComplete(new int[0], new Corporation[0]);
                }
                break;
            case TAKE_MERGER:
                action = true;
                Corporation[] involved = (Corporation[]) event.getMessage();
                Corporation parent = involved[0];
                Corporation child = involved[1];
                MergerPanel panel = new MergerPanel(parent, child, player, Color.BLACK,
                        Color.WHITE);
                panel.setupAndDisplayGUI(new Rectangle(100,100,700,700));
                panel.addListener(this);
                break;
            case CREATE_CORPORATION:
                {
                    Corporation[] opts = (Corporation[]) event.getMessage();
                    String[] names = new String[opts.length];
                    for(int i = 0; i < opts.length; i++)
                        names[i] = opts[i].getCorporateName();
                    JFrame compnayChoicePane = new JFrame();
                    String chosen = null;
                    do {
                        chosen = (String)JOptionPane.showInputDialog(
                                            compnayChoicePane,
                                            "Choose which corporation you wish to create",
                                            "Create Corporation",
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            names,
                                            names[0]);
                    } while(chosen == null);
                    Corporation chosenCompany = null;
                    for(int i = 0; i < opts.length; i++)
                        if(chosen.equals(opts[i].getCorporateName()))
                            chosenCompany = opts[i];
                    client.sendObject(EventType.createEvent(EventType.CORPORATION_CREATED, chosenCompany));
                }
                break;
            case CHOOSE_WINNER:
                {
                    Corporation[] opts = (Corporation[]) event.getMessage();
                    String[] names = new String[opts.length];
                    for(int i = 0; i < opts.length; i++)
                        names[i] = opts[i].getCorporateName();
                    JFrame compnayChoicePane = new JFrame();
                    String chosen = null;
                    do {
                        chosen = (String)JOptionPane.showInputDialog(
                                            compnayChoicePane,
                                            "Choose which corporation you want to win the merger",
                                            "Choose Merger Winer",
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            names,
                                            names[0]);
                    } while(chosen == null);
                    Corporation chosenCompany = null;
                    for(int i = 0; i < opts.length; i++)
                        if(chosen.equals(opts[i].getCorporateName()))
                            chosenCompany = opts[i];
                    client.sendObject(EventType.createEvent(EventType.MERGER_WINNER, chosenCompany));
                }
                break;
            case CHOOSE_FIRST:
                {
                    List<Corporation> options = new ArrayList<>(Arrays.asList((Corporation[]) event.getMessage()));
                    String[] names = new String[options.size()];
                    for(int i = 0; i < options.size(); i++)
                        names[i] = options.get(i).getCorporateName();
                    JFrame compnayChoicePane = new JFrame();
                    String chosen = null;
                    do {
                        chosen = (String)JOptionPane.showInputDialog(
                                            compnayChoicePane,
                                            "Choose corporation to be eaten first",
                                            "Merger Choice",
                                            JOptionPane.QUESTION_MESSAGE,
                                            null,
                                            names,
                                            names[0]);
                    } while(chosen == null);
                    Corporation chosenCompany = null;
                    for(int i = 0; i < options.size(); i++)
                        if(chosen.equals(options.get(i).getCorporateName()))
                            chosenCompany = options.get(i);
                    client.sendObject(EventType.createEvent(EventType.MERGED_FIRST, chosenCompany));
                }
                break;
            case PLAY_PIECE:
                manager.allowBoardPlacement();
                //System.out.println(player.getName());
                break;
            case GAME_END:
                Investor[] allPlayers = (Investor[]) event.getMessage();
                String endMessage = "";
                for(int i = 0; i < allPlayers.length; i++)
                    endMessage += (i+1) + ". " + allPlayers[i].getName() + " $" + allPlayers[i].getMoney() +"\n";
                JFrame frame = new JFrame();
                JOptionPane.showMessageDialog(frame, endMessage,
                        allPlayers[0].getName() + " wins",
                        JOptionPane.INFORMATION_MESSAGE);
                break;
            case REMOVE_TILES:
                //remove un-playable tiles
                List<HotelView> hotels = new ArrayList<>();
                for(Hotel h : (Hotel[])event.getMessage())
                {
                    if(!AcquireRules.canPieceBePlayed(h, board))
                        hotels.add(new HotelView(h, Color.BLACK, "TIMES NEW ROMAN"));
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
                break;
        }
        view.update();
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
            if(company.getNumberOfHotels() >= AcquireRules.SAFE_CORPORATION_SIZE)
                numSave++;
        }
        //check if the played location lines up with the hotel's location
        boolean isValid = placed.getLocation().equals(loc) && numSave < 2;
        if(!isValid) {
            player.addPieceToHand(board.remove(loc.getRow(), loc.getCol()));
        }
        else
        {
            //it worked, tell the server what happened.
            client.sendObject(EventType.createEvent(EventType.PIECE_PLAYED, placed));
            manager.disallowBoardPlacement();
        }
        
        view.update();
    }

    private boolean sendStocks = false;
    
    @Override
    public void buyingComplete(int[] stocksBought, Corporation[] companies) 
    {
        if(sendStocks)
        {
            sendStocks = false;
            //System.out.println("BUYING STUFF: " + stocksBought + " " + companies);
            List<Stock> stocks = new ArrayList<>();
            for(int company = 0; company < companies.length; company++)
            {
                for(int i = 0; i < stocksBought[company]; i++)
                    stocks.add(companies[company].getStock());
            }
            client.sendObject(EventType.createEvent(EventType.STOCKS_BOUGHT, stocks.toArray(new Stock[stocks.size()])));
        }
    }
    
    private boolean action = false;

    @Override
    public void finished(Corporation parent, Corporation child, int kept, int sold, int traded) 
    {
        if(action)
        {
            action = false;
            client.sendObject(EventType.createEvent(EventType.MERGER_ACTION, new int[]{sold, traded}));
        }
    }

    @Override
    public void finished(Component[] options, boolean[] states)
    {
        List<Hotel> removed = new ArrayList<>();
        for(int i = 0; i < options.length; i++)
        {
            if(!states[i])
                removed.add(((HotelView)options[i]).getHotel());
        }
        client.sendObject(EventType.createEvent(EventType.TILES_REMOVED, 
                removed.toArray(new Hotel[removed.size()])));
    }
}
