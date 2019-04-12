/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/

package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.PackedScore;

public final class Score {

    private final static int zero = 0;
    private static final int turnTrickSize = 4;
    private static final int turnPointsSize = 9;
    private static final int gamePointsSize = 11;
    private static final int gamePointsStart = 13;

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

    public static final Score INITIAL = new Score(zero, zero, zero, zero, zero,
            zero);

    /**
     * @param pkScore
     *            The packed version of the score
     * @return The unpacked version of the score
     */
    public static Score ofPacked(long pkScore) {
        // Preconditions.checkArgument(PackedScore.isValid(pkScore));

        int nbP1 = (int) Bits64.extract(pkScore, zero, turnTrickSize);
        int tP1 = (int) Bits64.extract(pkScore, turnTrickSize, turnPointsSize);
        int gP1 = (int) Bits64.extract(pkScore, gamePointsStart,
                gamePointsSize);
        int nbP2 = (int) Bits64.extract(pkScore, Integer.SIZE, turnTrickSize);
        int tP2 = (int) Bits64.extract(pkScore, Integer.SIZE + turnTrickSize,
                turnPointsSize);
        int gP2 = (int) Bits64.extract(pkScore, Integer.SIZE + gamePointsStart,
                gamePointsSize);

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
        return PackedScore.gamePoints(this.packed(), t);
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
        Preconditions.checkArgument(trickPoints >= 0);

        return ofPacked(PackedScore.withAdditionalTrick(this.packed(),
                winningTeam, trickPoints));
    }

    /**
     * @return The final of both teams score of this turn
     */
    public Score nextTurn() {

        return ofPacked(PackedScore.nextTurn(this.packed()));
    }

    @Override
    public int hashCode() {
        return Long.hashCode(this.packed());
    }

    @Override
    public String toString() {
        return PackedScore.toString(this.packed());
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Score)
            return this.packed() == ((Score) other).packed();
        return false;

    }
}
