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

import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Game;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import java.awt.Color;
import java.util.ArrayList;
import java.util.List;

/**
 * Class meant to test the controller elements.
 * @author Nicholas Maltbie
 */
public class ControllerTest
{
    public static void main(String[] args)
    {
        Investor investor = new Investor("Nick", 6000, 6, Color.RED);
        String[] names = {"CUP1", "CPU2"};
        Color[] colors = {Color.BLUE, Color.GREEN};
        Game game = new Game(names, colors);
        List<Corporation> companies = new ArrayList<>();
        for(int i = 0; i < game.getNumCorporations(); i++)
            companies.add(game.getCorporation(i));
        List<Investor> players = new ArrayList<>();
        for(int i = 0; i < game.getNumInvestors(); i++)
            players.add(game.getInvestor(i));
        AcquireMachine machine = new AcquireMachine(game.getGameBoard(),
            companies, game.getMarket(), players, investor, (int)(Math.random()*
            (players.size()+1)));
        
        game.getMarket().shuffle();
        for(int i = 0; i < 20; i++)
        {
            Hotel h = game.getMarket().draw();
            game.getGameBoard().set(h.getLocation().getRow(), 
                    h.getLocation().getCol(), h);
        }
        
        machine.start();
    }
}
