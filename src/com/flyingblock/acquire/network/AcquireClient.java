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

import com.flyingblock.acquire.model.Board;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.view.GameView;
import java.util.List;

/**
 * A client that work with an acquire host and react to events sent from the host.
 * @author Nicholas Maltbie
 */
public class AcquireClient implements ClientListener
{
    /**Connection to the server*/
    private Client client;
    /**Game board*/
    private Board<Hotel> board;
    /**Game players*/
    private List<Investor> investors;
    /**Companies connected to the server*/
    private List<Corporation> companies;
    /**GameView to help the player*/
    private GameView view;
    
    /**
     * Creates an acquire client from a client that is already connected to a server
     * @param client Client that is connected to the server.
     */
    public AcquireClient(Client client)
    {
        client.addListener(this);
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
        switch(type)
        {
            case BOARD_UPDATE:
                Board<Hotel> update = (Board) event.getMessage();
                for(int r = 0; r < board.getNumRows(); r++)
                    for(int c = 0; c < board.getNumCols(); c++)
                        board.set(r, c, update.get(r, c));
                break;
            case PLAYERS_UPDATE:
                Investor[] update = (Investor[]) event.getMessage();
                for(Investor player : update)
                {
                    for(int i = 0; i < investors.size(); i++)
                    {
                        if(player.getName().equals(investors.get(i).getName()))
                        {
                            Investor edit = investors.get(i);
                            edit.setMoney(player.getMoney());
                            edit.clearStocks();
                            edit.addStocks(edit.getStocks());
                            //finish this
                        }
                    }
                }
                break;
            case CORPORATIONS_UPDATE:
                
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
            default:
                
                break;
        }
    }
}
