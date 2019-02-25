/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.PackedScore;


public final class Score {

    private long nbPlis1, turnPoints1, gamePoints1;
    private long nbPlis2, turnPoints2, gamePoints2;


    private Score(long nbP1, long tP1, long gP1, long nbP2, long tP2, long gP2) {
        nbPlis1=nbP1; 
        turnPoints1 = tP1; 
        gamePoints1 = gP1; 
        nbPlis2 = nbP2; 
        turnPoints2 = tP2; 
        gamePoints2 = gP2; 
    }

    public static final Score INITIAL = new Score(0,0,0,0,0,0);
    
    
    public Score ofPacked(long pkScore) {
        if(!PackedScore.isValid(pkScore))
            throw new IllegalArgumentException(); 
        
        long nbP1 = Bits64.extract(pkScore, 0,4); 
        long tP1 = Bits64.extract(pkScore, 4,9);
        long gP1 = Bits64.extract(pkScore, 13,11);
        long nbP2 = Bits64.extract(pkScore, 32,4);
        long tP2 = Bits64.extract(pkScore, 36,9);
        long gP2 = Bits64.extract(pkScore, 45,11);
        
        return new Score(nbP1,tP1,gP1,nbP2,tP2,gP2); 
    }
}
