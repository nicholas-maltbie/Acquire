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

import com.flyingblock.acquire.model.AcquireBoard;
import com.flyingblock.acquire.model.Corporation;
import com.flyingblock.acquire.model.Hotel;
import com.flyingblock.acquire.model.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

/**
 * A class that contains rules for the acquire board game and is implemented
 * as a utility class.
 * @author Nicholas Maltbie
 */
public class AcquireRules 
{
    /**
     * Random names
     */
    public static final String[] RANDOM_NAMES = {"MARX", "KEYNES", "SMITH", "MARSHALL",
        "GRAHAM", "KAHNEMAN", "MILTON", "LUXEMBURG", "GREENSPAN", "BEN", "ASDF", "BENELUX",
        "NICKROMANCER", "PERSON", "HUE MAN", "JANA", "DRAUG", "FLYGUY", "ESCMO", "BUDEW",
        "PIKACHU", "RIACHU", "L", "Q", "BISMARCK", "NONAME", "DEFAULT", "DEFACTO",
        "LENNIN", "SWEED", "NOTCH", "TRUMP", "GATES", "LE GUIN", "AVALON", "HILL",
        "NAME_43", "WINNER", "LOSER", "RANDOM", "NOT RANDOM", "#HASHTAG"};
    /**
     * The size that a corporation needs to be in order to be safe.
     */
    public static final int SAFE_CORPORATION_SIZE = 11;
    /**
     * End game size of a corporation
     */
    public static final int END_CORPORATION_SIZE = 41;
    /**
     * Checks if a piece can be played. A piece would not be able to be played
     * it connected two or more safe corporations.
     * @param hotel Hotel to check.
     * @param board Board to check validity of.
     * @return Returns if the piece can be played.
     */
    public static boolean canPieceBePlayed(Hotel hotel, AcquireBoard board)
    {
        Location loc = hotel.getLocation();
        board.set(loc.getRow(), loc.getCol(), hotel);
        int numSafe = 0;
        for(Corporation c : board.getCorporationsInBlob(loc.getRow(), loc.getCol()))
            if(c.getNumberOfHotels() >= SAFE_CORPORATION_SIZE)
               numSafe++;
        
        board.remove(loc.getRow(), loc.getCol());
        return numSafe <= 1;
    }
    /**
     * Gets a random name.
     * @return 
     */
    public static String getRandomName()
    {
        return RANDOM_NAMES[(int)(RANDOM_NAMES.length)];
    }
    
    public static List<String> getRandomNames(int num, Collection<String> filter)
    {
        return getRandomNames(num, new Random(), filter);
    }
    
    public static List<String> getRandomNames(int num, Random random, Collection<String> filter)
    {
        ArrayList<String> names = new ArrayList<>(Arrays.asList(RANDOM_NAMES));
        names.removeAll(filter);
        ArrayList<String> chosen = new ArrayList<>();
        for(int i = 0; i < num && i < names.size(); i++)
            chosen.add(names.remove(random.nextInt(names.size())));
        return chosen;
    }
}
