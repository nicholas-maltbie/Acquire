/**
 * 
 */
package com.flyingblock.acquire.model;

/**
 * Every Corporation/Stock has a market value which will determine the value of their
 * Stocks based on the number of hotels that the Corporation contains.
 * @author Nicholas Maltbie
 */
public enum MarketValue {
    LOW,
    MEDIUM,
    HIGH;
    
    /**
     * Based on the company's level, this will return the majority
     * shareholder bonus.
     * @param level Level of the company determined by the getLevel
     * method.
     * @return Returns the majority bonus value in dollars. 
     */
    public static int getMajorityBonus(int level)
    {
        return (level+2)*1000;
    }
    
    /**
     * Based on the company's level, this will return the minority
     * shareholder bonus.
     * @param level Level of the company determined by the getLevel
     * method.
     * @return Returns the minority bonus value in dollars. 
     */
    public static int getMinorityBonus(int level)
    {
        return (level+2)*500;
    }
    
    /**
     * Based on the company's level, this will return the stock trade in
     * value when the company is consumed
     * @param level Level of the company determined by the getLevel
     * method.
     * @return Returns the majority bonus value in dollars. 
     */
    public static int getStockValue(int level)
    {
        return (level+2)*100;
    }
    
    /**
     * The cutoff numbers from the number of hotels for different 
     * stock/shareholder values.
     */
    public static int[] LEVEL_CUTOFFS = {2,3,4,5,10,20,30,40};
    
    /**
     * Gets the stock level for a product based on MarketValue and
     * the number of hotels owned by a company.
     * @param value The market level, LOW has no bonus, MEDIUSM has a
     * one level bonus and HIGH has a two level bonus.
     * @param numHotels The number of hotels that the company 
     * has.
     * @return Returns the market level that can be used to find 
     * dollar values for stocks and shareholder bonuses.
     */
    public static int getLevel(MarketValue value, int numHotels)
    {
        int base = value.ordinal();
        for(int level = 0; level < LEVEL_CUTOFFS.length; level++)
        {
            if(numHotels <= LEVEL_CUTOFFS[level])
            {
                return level+base;
            }
        }
        return LEVEL_CUTOFFS.length + base;
    }
    
    /**
     * Gets the level of a corporation, uses corporation's values to
     * execute the method getLevel(MarketValue value, int numHotels).
     * @param corporation Corporation to get the level of.
     * @param value MarketValue that affects the level.
     * @return Returns the market level that can be used to find 
     * dollar values for stocks and shareholder bonuses.
     */
    public static int getLevel(MarketValue value, Corporation corporation)
    {
        return getLevel(value, corporation.getNumberOfHotels());
    }
}
