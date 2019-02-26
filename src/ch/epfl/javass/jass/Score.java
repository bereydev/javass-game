/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/


package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.PackedScore;

public final class Score {

    private int turnTricks1, turnPoints1, gamePoints1;
    private int turnTricks2, turnPoints2, gamePoints2;

    /**
     * @param tT1
     *            The turn tricks of TEAM_1
     * @param tP1
     *            The turn points of TEAM_1
     * @param gP1
     *            The game points of TEAM_1
     * @param nbP2
     *            The turn tricks of TEAM_2
     * @param tT2
     *            The turn points of TEAM_2
     * @param gP2
     *            The game points of TEAM_2
     */
    private Score(int tT1, int tP1, int gP1, int nbP2, int tT2, int gP2) {
        turnTricks1 = tT1;
        turnPoints1 = tP1;
        gamePoints1 = gP1;
        turnTricks2 = nbP2;
        turnPoints2 = tT2;
        gamePoints2 = gP2;
    }

    public static final Score INITIAL = new Score(0, 0, 0, 0, 0, 0);

    /**
     * @param pkScore
     *            The packed version of the score
     * @return The unpacked version of the score
     */
    public static Score ofPacked(long pkScore) {
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

    /**
     * @return The packed version of the score
     */
    public long packed() {
        return PackedScore.pack(turnTricks1, turnPoints1, gamePoints1,
                turnTricks2, turnPoints2, gamePoints2);
    }

    /**
     * @param t
     *            The team 't'
     * @return The number of tricks the team 't' has won during this turn
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(this.packed(), t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The number of points team 't' has won during this turn
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(this.packed(), t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The amount of points the team has until before the current turn
     */
    public int gamePoints(TeamId t) {
        return PackedScore.turnPoints(this.packed(), t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The total amount of points that team has won until now
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(this.packed(), t);
    }

    /**
     * @param winningTeam
     *            The team that won the trick
     * @param trickPoints
     *            The total points of this trick
     * @return The score of both teams after this trick
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        if (trickPoints < 0)
            throw new IllegalArgumentException();

        return ofPacked(PackedScore.withAdditionalTrick(this.packed(),
                winningTeam, trickPoints));
    }

    /**
     * @return The final of both teams score of this turn
     */
    public Score nextTurn() {

        return ofPacked(PackedScore.nextTurn(this.packed()));
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return Long.hashCode(this.packed());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "(" + turnTricks1 + "," + turnPoints1 + "," + gamePoints1 + ")/("
                + turnTricks2 + "," + turnPoints2 + "," + gamePoints2 + ")";
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object other) {
        if (other.getClass() != this.getClass())
            return false;

        return this.packed() == ((Score) other).packed();
    }
}
