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
     * 
     * @return 
     */
    public Investor getPlayer()
    {
        return player;
    }
    
    public AcquireBoard getBoard()
    {
        return board;
    }
    
    public List<Corporation> getCompanies()
    {
        return companies;
    }
    
    public List<Investor> getOpponents()
    {
        return opponents;
    }
    
    abstract public void playTile();
    abstract public Corporation createCorporation(List<Corporation> options);
    abstract public Corporation choseMergerWinner(List<Corporation> options);
    abstract public void reactToMerger(Corporation parent, Corporation child);
    abstract public void buyStocks();
}
