/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.computer;

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import java.util.List;

/**
 * The start of a computer AI system. Overall system for an opponent. It makes
 * decisions.
 * @author Nicholas Maltbie
 */
public abstract class Decider
{
    /**
     * Player that this decider makes moves for.
     */
    private Investor player;
    /**
     * Game board.
     */
    private AcquireBoard board;
    /**
     * Companies in the game.
     */
    private List<Corporation> companies;
    /**
     * Opponents of the Decider. 
     */
    private List<Investor> opponents;
    
    /**
     * Constructs a Decider.
     * @param player Investor that the decider makes decisions for.
     * @param board Game board that the decider plays on.
     * @param companies Companies that are in the game.
     * @param opponents Opponent players in the game (including the human ones).
     */
    public Decider(Investor player, AcquireBoard board, List<Corporation> companies,
            List<Investor> opponents)
    {
        this.player = player;
        this.board = board;
        this.companies = companies;
        this.opponents = opponents;
    }
    
    /**
     * Gets the player for this decider.
     * @return Returns the investor for this decider.
     */
    public Investor getPlayer()
    {
        return player;
    }
    
    /**
     * Gets the board the decider participates in.
     * @return The AcquireBoard of the decider.
     */
    public AcquireBoard getBoard()
    {
        return board;
    }
    
    /**
     * Gets the companies in the game.
     * @return Returns a list of the companies in the game.
     */
    public List<Corporation> getCompanies()
    {
        return companies;
    }
    
    /**
     * Gets the opponents of the player.
     * @return Returns a list of the opponents.
     */
    public List<Investor> getOpponents()
    {
        return opponents;
    }
    
    
    /**
     * Prompts the decider to sell, trade in or hold all of his or her stocks in
     * the child for those in the parent based on the actions of getMergerActions.
     * @param parent Parent Corporation that is consuming the child corporation.
     * @param child Corporation that is being consumed by the parent in the merger.
     */
    public void reactToMerger(Corporation parent, Corporation child)
    {
        int[] opts = getMergerActions(parent, child);
        
        int traded = opts[1];
        for(int i = 0; i < traded/2; i++)
        {
            child.returnStock(getPlayer().removeStock(child));
            child.returnStock(getPlayer().removeStock(child));
            getPlayer().addStock(parent.getStock());
        }
        int sold = opts[0];
        for(int i = 0; i < sold; i++)
        {
            child.returnStock(getPlayer().removeStock(child));
            getPlayer().addMoney(child.getStockPrice());
        }
    }
    
    /**
     * Gets which piece the decider wishes to play from his or her hand. It must 
     * be a valid play, if not, it will be removed from the player's hand and
     * the decider will be re-prompted to get a play option.
     * @return Returns the hotel that the player wishes to play from his or her
     * hand.
     */
    abstract public Hotel getPlayOption();
    
    /**
     * Requests that the player decides to create a corporation given the 
     * options available.
     * @param options Options for the decider.
     * @return Return the corporation that the decider wishes to create.
     */
    abstract public Corporation createCorporation(List<Corporation> options);
    
    /**
     * Chooses a merger winner out of possible mergers.
     * @param options Companies involved in the merger.
     * @return Merger that is chosen to win.
     */
    abstract public Corporation choseMergerWinner(List<Corporation> options);
    
    /**
     * Requests the decider to chose which of the follow companies should be merged.
     * @param options Corporations that may be dissolved but are the same size.
     * @param winner Corporation that will dissolve the options but dissolving 
     * the chosen option first.
     * @return Returns the Corporation that the decider wants to win the corporation first.
     */
    abstract public Corporation choseFirstDissolved(List<Corporation> options, Corporation winner);
     
    /**
     * Prompts the choose its actions to sell, trade in or hold all of his or her stocks in
     * the child for those in the parent.
     * @param parent Parent Corporation that is consuming the child corporation.
     * @param child Corporation that is being consumed by the parent in the merger.
     * @return Returns the actions that the decider wishes to take took in the format:
     *  {sold, traded}.
     */
    abstract public int[] getMergerActions(Corporation parent, Corporation child);
    
    /**
     * Prompts the decider to buy stocks from the Established companies within
     * the current acquire board.
     * @param established Corporations currently established on the board.
     */
    abstract public void buyStocks(List<Corporation> established);
}
