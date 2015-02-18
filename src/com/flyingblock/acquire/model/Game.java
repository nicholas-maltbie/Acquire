/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire.model;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

/**
 * A game represents all aspects of an Acquire game model. It doesn't do
 * anything, just stores the data for the game.
 * @author Nicholas Maltbie
 */
public class Game 
{
    /**
     * The region for the game.
     */
    private AcquireBoard gameBoard;
    /**
     * Saved data for players within the game.
     */
    private List<Investor> players;
    /**
     * Companies that participate in the game.
     */
    private List<Corporation> companies;
    /**
     * Hotel market for the game.
     */
    private HotelMarket gameTiles;
    
    /**
     * Constructs a game with players, companies, and a specified board size.
     * @param players Player for the game.
     * @param companies Companies participating in the game.
     * @param boardSize Size of the playing board.
     */
    public Game(List<Investor> players, List<Corporation> companies, Dimension boardSize)
    {
        this.players = players;
        this.companies = companies;
        gameBoard = new AcquireBoard((int)boardSize.getHeight(), (int)boardSize.getWidth());
        gameTiles = new HotelMarket(gameBoard);
    }
    
    /**
     * Default company names.
     */
    private static final String[] DEFAULT_COMPANIES = {"TOWER", "NICKLANDIA"};
    /**
     * Default company market values.
     */
    private static final MarketValue[] DEFAULT_VALUES = {MarketValue.LOW, MarketValue.HIGH};
    
    /**
     * Constructs a default game with variable player names.
     * @param playerNames Names of participating players.
     */
    public Game(String[] playerNames)
    {
        gameBoard = new AcquireBoard();
        gameTiles = new HotelMarket(gameBoard);
        companies = new ArrayList<>(DEFAULT_COMPANIES.length);
        players = new ArrayList<>(playerNames.length);
        for(String name : playerNames)
            players.add(new Investor(name, 6000, 6));
        for(int company = 0; company < DEFAULT_COMPANIES.length; company++)
            companies.add(new Corporation(DEFAULT_COMPANIES[company], gameBoard, DEFAULT_VALUES[company]));
    }
    
    /**
     * Constructs a default game with completely variable players.
     * @param players Investors participating in the game.
     */
    public Game(List<Investor> players)
    {
        this.players = players;
        gameBoard = new AcquireBoard();
        gameTiles = new HotelMarket(gameBoard);
        companies = new ArrayList<>(DEFAULT_COMPANIES.length);
        for(int company = 0; company < DEFAULT_COMPANIES.length; company++)
            companies.add(new Corporation(DEFAULT_COMPANIES[company], gameBoard, DEFAULT_VALUES[company]));
    }
    
    /**
     * Gets the number of players in the game.
     * @return Returns the number of players in the game.
     */
    public int getNumInvestors()
    {
        return players.size();
    }
    
    /**
     * Gets an investor in the game.
     * @param index Index of specified investor.
     * @return Returns the investor at the specified index.
     */
    public Investor getInvestor(int index)
    {
        return players.get(index);
    }
    
    /**
     * Gets the game board.
     * @return Returns the gameBoard field.
     */
    public AcquireBoard getGameBoard()
    {
        return gameBoard;
    }
    
    /**
     * Gets the number of corporations in the game.
     * @return Returns the number of corporations in the game.
     */
    public int getNumCorporations()
    {
        return companies.size();
    }
    
    /**
     * Gets a corporation in the game.
     * @param index Index of specified corporation.
     * @return Returns the corporation at the specified index.
     */
    public Corporation getCorporation(int index)
    {
        return companies.get(index);
    }
    
    /**
     * Gets the HotelMarket for the game.
     * @return Returns the gameTiles field.
     */
    public HotelMarket getMarket()
    {
        return  gameTiles;
    }
}
