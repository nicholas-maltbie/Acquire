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

/**
 *
 * @author Nicholas Maltbie
 */
public class ModelTest {
    public static void main(String[] args)
    {
        AcquireBoard board = new AcquireBoard();
        board.set(1, 1, new Hotel(new Location(1,1)));
        board.set(1, 2, new Hotel(new Location(1,2)));
        board.set(1, 3, new Hotel(new Location(1,3)));
        board.set(2, 3, new Hotel(new Location(2,3)));
        System.out.println(board);
        
        System.out.println(board.getBlob(1,1));
        
        Corporation corporation = new Corporation("Nicklandia", board, MarketValue.HIGH);
        corporation.setHeadquarters(new Location(1,1));
        corporation.incorporateRegoin();
        System.out.println(corporation);
        
        Stock stock = new Stock(corporation, MarketValue.HIGH);
        System.out.println(stock.getTradeInValue());
        System.out.println(corporation.getMajorityBonus());
        System.out.println(corporation.getMinorityBonus());
        
        System.out.println(board);
        System.out.println(board.getBlob(1,1));
    }
}
