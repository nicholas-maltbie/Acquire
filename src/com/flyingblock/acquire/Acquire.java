/*
 *
 *Acquire
 *
 *This is a license header!!! :D
 *
 *
 *
 */
package com.flyingblock.acquire;

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;
import com.flyingblock.acquire.model.MarketValue;
import com.flyingblock.acquire.model.Stock;

/**
 *
 * @author Nicholas Maltbie
 */
public class Acquire {
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
