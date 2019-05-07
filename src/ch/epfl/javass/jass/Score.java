/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/

package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits64;
import ch.epfl.javass.jass.PackedScore;

public final class Score {

    private final long pkScore;

    /**
     * @param pkScore
     *            the packedScore
     */
    private Score(long pkScore) {
        this.pkScore = pkScore;
    }

    public static final Score INITIAL = new Score(PackedScore.INITIAL);

    /**
     * @param pkScore
     *            The packed version of the score
     * @return The unpacked version of the score
     */
    public static Score ofPacked(long pkScore) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore));
        return new Score(pkScore);
    }

    /**
     * @return The packed version of the score
     */
    public long packed() {
        return pkScore;
    }

    /**
     * @param t
     *            The team 't'
     * @return The number of tricks the team 't' has won during this turn
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(pkScore, t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The number of points team 't' has won during this turn
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(pkScore, t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The amount of points the team has until before the current turn
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(pkScore, t);
    }

    /**
     * @param t
     *            The team 't'
     * @return The total amount of points that team has won until now
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(pkScore, t);
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

        return ofPacked(PackedScore.withAdditionalTrick(pkScore,
                winningTeam, trickPoints));
    }

    /**
     * @return The final of both teams score of this turn
     */
    public Score nextTurn() {
        return ofPacked(PackedScore.nextTurn(pkScore));
    }

    @Override
    public int hashCode() {
        return Long.hashCode(pkScore);
    }

    @Override
    public String toString() {
        return PackedScore.toString(pkScore);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof Score && pkScore == ((Score) other).pkScore;
    }
}
