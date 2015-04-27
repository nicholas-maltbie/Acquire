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

import com.flyingblock.acquire.computer.Decider;
import com.flyingblock.acquire.computer.RandomDecider;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Game;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Investor;
import com.flyingblock.acquire.model.Location;
import java.awt.Color;
import java.io.IOException;
import java.net.Inet4Address;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class meant to test the acquire client and acquire server.
 * @author Maltbie_N
 */
public class TestAcquire 
{
    public static void main(String[] args)
    {
        Server server = new Server(44);
        new java.util.Timer().schedule( 
            new java.util.TimerTask() {
                @Override
                public void run() {
                    try {
                        server.start();
                    } catch (IOException ex) {
                        Logger.getLogger(TestAcquire.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }, 0);
        String address = "localhost";
        try {
            address = Inet4Address.getLocalHost().getHostAddress();
        } catch (UnknownHostException ex) {
            Logger.getLogger(TestAcquire.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        String[] names = {"Smith", "Marx", "Keynes", "Q"};
        Color[] colors = {Color.BLUE, Color.GREEN, Color.YELLOW, Color.MAGENTA};
        Game game = new Game(names, colors);
        List<Corporation> companies = new ArrayList<>();
        for(int i = 0; i < game.getNumCorporations(); i++)
            companies.add(game.getCorporation(i));
        List<Investor> players = new ArrayList<>();
        for(int i = 0; i < game.getNumInvestors(); i++)
            players.add(game.getInvestor(i));
        
        game.getMarket().shuffle();
        
        List<Corporation> taken = game.getGameBoard().getCompaniesOnBoard();
        List<Corporation> available = new ArrayList<>();
        for(Corporation company : companies)
            if(!taken.contains(company))
                available.add(company);
        
        List<Decider> deciders = new ArrayList<>();
        for(int i = 0; i < 2; i++)
        {
            deciders.add(new RandomDecider(players.get(i), game.getGameBoard(), companies, players));
        }
        List<NetInvestor> netPlayers = new ArrayList<>();
        for(int i = 2; i < players.size(); i++)
        {
            try {
                Client client = new Client(address, 44);
                new java.util.Timer().schedule(
                        new java.util.TimerTask() {
                            @Override
                            public void run() {
                                client.start();
                            }
                        }, 0);
                AcquireClient acquireClient = new AcquireClient(client, game.getGameBoard(),
                        players, companies, players.get(i));
                
                acquireClient.initView();
            } catch (IOException ex) {
                Logger.getLogger(TestAcquire.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        int player = 2;
        for(ClientThread thread : server.getClients())
        {
            netPlayers.add(new NetInvestor(players.get(player), thread));
            player++;
        }
        AcquireServer acquireServer = new AcquireServer(server, deciders, 
                netPlayers, players, game.getGameBoard(), 
                companies, game.getMarket(), 100);
            
        acquireServer.start();
    }
}
