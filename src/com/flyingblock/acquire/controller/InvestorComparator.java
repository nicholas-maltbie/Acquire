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

import com.flyingblock.acquire.model.Investor;
import java.util.Comparator;

/**
 * Compares investor by money, investors with the most money will be sorted at 
 * the lowest numbers of the list.
 * @author Nick_Pro
 */
public class InvestorComparator implements Comparator<Investor>
{
    @Override
    public int compare(Investor o1, Investor o2) 
    {
        if(o1.getMoney() > o2.getMoney())
            return -1;
        else if(o1.getMoney() < o2.getMoney())
            return 1;
        return 0;
    }
    
}
