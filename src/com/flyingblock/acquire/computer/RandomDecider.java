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

import com.flyingblock.acquire.controller.AcquireRules;
import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * A decider that makes all its decisions based of of a random number generator.
 * @author Nicholas Maltbie
 */
public class RandomDecider extends Decider
{
    /**
     * Random number generator.
     */
    private Random rng;
    
    /**
     * Constructs a RandomDecider with a given random number generator.
     * @param player Investor that the decider makes decisions for.
     * @param board Game board that the decider plays on.
     * @param companies Companies that are in the game.
     * @param opponents Opponent players in the game (including the human ones).
     * @param rng Random number generator for the decisions.
     */
    public RandomDecider(Investor player, AcquireBoard board, List<Corporation> companies,
            List<Investor> opponents, Random rng)
    {
        super(player, board, companies, opponents);
        this.rng = rng;
    }
    
     /**
     * Constructs a RandomDecider with a given random random number generator.
     * @param player Investor that the decider makes decisions for.
     * @param board Game board that the decider plays on.
     * @param companies Companies that are in the game.
     * @param opponents Opponent players in the game (including the human ones).
     */
    public RandomDecider(Investor player, AcquireBoard board, List<Corporation> companies,
            List<Investor> opponents)
    {
        this(player, board, companies, opponents, new Random());
    }

    @Override
    public Hotel getPlayOption() 
    {
        List<Hotel> playablePieces = new ArrayList<>();
        for(int i = 0; i < getPlayer().getHandSize(); i++)
        {
            if(getPlayer().getFromHand(i) != null && AcquireRules.canPieceBePlayed(getPlayer().getFromHand(i), getBoard()))
                playablePieces.add(getPlayer().getFromHand(i));
        }
        return playablePieces.get(rng.nextInt(playablePieces.size()));
    }

    @Override
    public Corporation createCorporation(List<Corporation> options) 
    {
        return options.get(rng.nextInt(options.size()));
    }

    @Override
    public Corporation choseMergerWinner(List<Corporation> options) 
    {
        return options.get(rng.nextInt(options.size()));
    }

    @Override
    public Corporation choseFirstDissolved(List<Corporation> options, Corporation winner) 
    {
        return options.get(rng.nextInt(options.size()));
    }
    
    @Override
    public void buyStocks(List<Corporation> established) 
    {
        for(int i = 0; i < 3; i++)
        {
            List<Corporation> canAfford = new ArrayList<>();
            for(Corporation e : established)
            {
                if(e.getStockPrice() <= getPlayer().getMoney() && e.getAvailableStocks() > 0)
                    canAfford.add(e);
            }
            if(!canAfford.isEmpty())
            {
                Corporation chosen = canAfford.get(rng.nextInt(canAfford.size()));
                getPlayer().addMoney(-chosen.getStockPrice());
                getPlayer().addStock(chosen.getStock());
            }
        }
    }

    @Override
    public int[] getMergerActions(Corporation parent, Corporation child) {
        int stocks = getPlayer().getStocks(child);
        int traded = 0;
        if(stocks > 4)
            traded = rng.nextInt(stocks/3)*2;
        stocks -= traded;
        int sold = rng.nextInt(stocks);
        return new int[]{sold, traded};
    }

    @Override
    public void removeNonPlayableTiles(List<Hotel> tiles) 
    {
        for(int i = 0; i < getPlayer().getHandSize(); i++)
        {
            if(getPlayer().getFromHand(i) != null && 
                    tiles.contains(getPlayer().getFromHand(i)))
                getPlayer().removeFromHand(i);
        }
        
    }
}
