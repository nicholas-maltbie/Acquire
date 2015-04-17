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
import com.flyingblock.acquire.controller.HumanPlayerFSM;
import com.flyingblock.acquire.controller.PieceManager;
import com.flyingblock.acquire.controller.PlayerListener;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Board;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.Stock;
import com.flyingblock.acquire.view.GameView;
import java.util.ArrayList;
import java.util.List;

/**
 * A client that work with an acquire host and react to events sent from the host.
 * @author Nicholas Maltbie
 */
public class AcquireClient implements ClientListener, PlayerListener
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
        client.addListener(this);
        this.client = client;
        this.board = board;
        this.investors = investors;
        this.companies = companies;
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
        manager.disallowBoardPlacement();
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
        System.out.println(type);
        switch(type)
        {
            case BOARD_UPDATE:
                Board<Hotel> update = (Board) event.getMessage();
                for(int r = 0; r < board.getNumRows(); r++)
                    for(int c = 0; c < board.getNumCols(); c++)
                        board.set(r, c, update.get(r, c));
                break;
            case PLAYERS_UPDATE:
                Investor[] playerUpdate = (Investor[]) event.getMessage();
                for(Investor player : playerUpdate)
                {
                    for(int i = 0; i < investors.size(); i++)
                    {
                        if(player.getName().equals(investors.get(i).getName()))
                        {
                            Investor edit = investors.get(i);
                            edit.addMoney(player.getMoney() - edit.getMoney());
                            edit.clearStocks();
                            edit.addStocks(edit.getStocks());
                        }
                    }
                }
                break;
            case CORPORATIONS_UPDATE:
                Corporation[] corps = (Corporation[]) event.getMessage();
                for(Corporation c : corps)
                {
                    for(int i = 0; i < corps.length; i++)
                    {
                        if(c.getCorporateName().equals(corps[i].getCorporateName()))
                        {
                            c.setAvailableStocks(corps[i].getAvailableStocks());
                            if(corps[i].isEstablished())
                                c.setHeadquarters(corps[i].getHeadquarters());
                            else if(c.isEstablished())
                                c.dissolve();
                        }
                    }
                }
                break;
            case BUY_STOCKS:
                
                break;
            case SELL_STOCKS:
                
                break;
            case TAKE_MERGER:
                
                break;
            case CREATE_CORPORATION:
                
                break;
            case CHOOSE_WINNER:
                
                break;
            case CHOOSE_FIRST:
                
                break;
            case INVALID_RESPONSE:
                
                break;
            case PLAY_PIECE:
                manager.allowBoardPlacement();
                break;
            default:
                
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
            return;
        }
        else
        {
            //it worked, tell the server what happened.
            client.sendObject(EventType.createEvent(EventType.PIECE_PLAYED, placed));
            manager.disallowBoardPlacement();
        }
    }
}
