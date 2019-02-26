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

    private int turnTricks1, turnPoints1, gamePoints1;
    private int turnTricks2, turnPoints2, gamePoints2;

    private Score(int nbP1, int tP1, int gP1, int nbP2, int tP2, int gP2) {
        turnTricks1 = nbP1;
        turnPoints1 = tP1;
        gamePoints1 = gP1;
        turnTricks2 = nbP2;
        turnPoints2 = tP2;
        gamePoints2 = gP2;
    }

    public static final Score INITIAL = new Score(0, 0, 0, 0, 0, 0);

    public Score ofPacked(long pkScore) {
        if (!PackedScore.isValid(pkScore))
            throw new IllegalArgumentException();

        int nbP1 = (int) Bits64.extract(pkScore, 0, 4);
        int tP1 = (int) Bits64.extract(pkScore, 4, 9);
        int gP1 = (int) Bits64.extract(pkScore, 13, 11);
        int nbP2 = (int) Bits64.extract(pkScore, 32, 4);
        int tP2 = (int) Bits64.extract(pkScore, 36, 9);
        int gP2 = (int) Bits64.extract(pkScore, 45, 11);

        return new Score(nbP1, tP1, gP1, nbP2, tP2, gP2);
    }

    public long packed() {
        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1,
                turnTricks2, turnPoints2, gamePoints2);
    }
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(this.packed(), t); 
    }
    public int turnPoints(TeamId t) {
        return 0; 
        
    }
}
