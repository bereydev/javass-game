/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.jass.PackedScore;


public final class Score {

    private int nbPlis1, turnPoints1, gamePoints1;
    private int nbPlis2, turnPoints2, gamePoints2;


    private Score(int nbP1, int tP1, int gP1, int nbP2, int tP2, int gP2) {
        nbPlis1=nbP1; 
        turnPoints1 = tP1; 
        gamePoints1 = gP1; 
        nbPlis2 = nbP2; 
        turnPoints2 = tP2; 
        gamePoints2 = gP2; 
    }

    public static final Score INITIAL = new Score(0,0,0,0,0,0);
    
    
    public Score ofPacked(long pkScore) {
        if(PackedScore.isValid(pkScore))
            throw new IllegalArgumentException(); 
        
        return INITIAL; 
    }
}
