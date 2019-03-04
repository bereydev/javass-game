
package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits64;

/**
 * @author Jonathan Bereyziat (282962)
 *
 */
public final class PackedScore {
    
    /**
     * Private constructor you can't instantiate
     */
    private PackedScore() {
    }

    public static final long INITIAL = 0;

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @return true if the pkScore is valid and false otherwise
     */
    public static boolean isValid(long pkScore) {
        long score1 = Bits64.extract(pkScore, 0, 32);
        long score2 = Bits64.extract(pkScore, 32, 32);
        long turnTricks1 = Bits64.extract(score1, 0, 4);
        long turnPoints1 = Bits64.extract(score1, 4, 9);
        long gamePoints1 = Bits64.extract(score1, 13, 11);
        long rest_1 = Bits64.extract(score1, 24, Integer.SIZE - 24);
        long turnTricks2 = Bits64.extract(score2, 0, 4);
        long turnPoints2 = Bits64.extract(score2, 4, 9);
        long gamePoints2 = Bits64.extract(score2, 13, 11);
        long rest_2 = Bits64.extract(score2, 24, Integer.SIZE - 24);
        return (turnTricks1 >= 0 && turnTricks1 <= 9)
                && (turnPoints1 >= 0 && turnPoints1 <= 257)
                && (gamePoints1 >= 0 && gamePoints1 <= 2000) && (rest_1 == 0)
                && (turnTricks2 >= 0 && turnTricks2 <= 9)
                && (turnPoints2 >= 0 && turnPoints2 <= 257)
                && (gamePoints2 >= 0 && gamePoints2 <= 2000) && (rest_2 == 0);
    }

    /**
     * @param turnTricks1
     *            the number of tricks won by the Team 1 during the current turn
     * @param turnPoints1
     *            the points won by the Team 1 during the current turn
     * @param gamePoints1
     *            the total points of the Team 1 during the game
     * @param turnTricks2
     *            the number of tricks won by the Team 2 during the current turn
     * @param turnPoints2
     *            the points won by the Team 2 during the current turn
     * @param gamePoints2
     *            the total points of the Team 2 during the game
     * @return all the informations about the scores of the game packed in a
     *         Long number
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {
        long b1 = Bits64.pack(turnTricks1, 4, turnPoints1, 9);
        long score1 = Bits64.pack(b1, 13, gamePoints1, 11);
        long b2 = Bits64.pack(turnTricks2, 4, turnPoints2, 9);
        long score2 = Bits64.pack(b2, 13, gamePoints2, 11);
        long scoreFinal = Bits64.pack(score1, Integer.SIZE, score2,
                Integer.SIZE);

        return scoreFinal;
    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @param t
     *            the Team you want to get the number of tricks
     * @return the number of tricks won by the Team t during the turn
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        if (t == TeamId.TEAM_1) {
            return (int) Bits64.extract(pkScore, 0, 4);
        } else {
            return (int) Bits64.extract(pkScore, 32, 4);
        }

    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @param t
     *            the Team you want to get the points of the turn
     * @return the points won by the Team t during the turn
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        if (t == TeamId.TEAM_1) {
            return (int) Bits64.extract(pkScore, 4, 9);
        } else {
            return (int) Bits64.extract(pkScore, 36, 9);
        }
    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @param t
     *            the Team you want to get the points of the game
     * @return the total points won by the Team t during the game
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        if (t == TeamId.TEAM_1) {
            return (int) Bits64.extract(pkScore, 13, 11);
        } else {
            return (int) Bits64.extract(pkScore, 45, 11);
        }
    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @param t
     *            the Team you want to get the total points (from the game +
     *            from the current turn)
     * @return the total points won by the Team t during the game and the
     *         current turn
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert (isValid(pkScore));
        return gamePoints(pkScore, t) + turnPoints(pkScore, t);
    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @param winningTeam
     *            the team that has won the current trick
     * @param trickPoints
     *            the number of points that are given by the current trick
     * @return the packed score with updated data after the end of a trick
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        assert (isValid(pkScore));
        int turnTricksWin = turnTricks(pkScore, winningTeam);
        int turnPointsWin = turnPoints(pkScore, winningTeam);
        int gamePointsWin = gamePoints(pkScore, winningTeam);
        int turnTricksLose = turnTricks(pkScore, winningTeam.other());
        int turnPointsLose = turnPoints(pkScore, winningTeam.other());
        int gamePointsLose = gamePoints(pkScore, winningTeam.other());
        turnTricksWin++;
        turnPointsWin += trickPoints;
        if (turnTricksWin == Jass.TRICKS_PER_TURN) {
            turnPointsWin += Jass.MATCH_ADDITIONAL_POINTS;
        }
        if (winningTeam == TeamId.TEAM_1) {
            return pack(turnTricksWin, turnPointsWin, gamePointsWin,
                    turnTricksLose, turnPointsLose, gamePointsLose);
        } else {
            return pack(turnTricksLose, turnPointsLose, gamePointsLose,
                    turnTricksWin, turnPointsWin, gamePointsWin);
        }

    }

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @return the packed score with reinitialized turn tricks and turn points
     *         and updated game score
     */
    public static long nextTurn(long pkScore) {
        assert (isValid(pkScore));
        int turnPoints1 = turnPoints(pkScore, TeamId.TEAM_1);
        int gamePoints1 = gamePoints(pkScore, TeamId.TEAM_1);
        int turnPoints2 = turnPoints(pkScore, TeamId.TEAM_2);
        int gamePoints2 = gamePoints(pkScore, TeamId.TEAM_2);
        gamePoints1 += turnPoints1;
        gamePoints2 += turnPoints2;

        return pack(0, 0, gamePoints1, 0, 0, gamePoints2);
    }

    /**
     * Used to debug purpose
     * 
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @return the String that represent the pkScore with decimal numbers in a
     *         custom layout
     */
    public static String toString(long pkScore) {
        assert (isValid(pkScore));

        return "(" + turnTricks(pkScore, TeamId.TEAM_1) + ","
                + turnPoints(pkScore, TeamId.TEAM_1) + ","
                + gamePoints(pkScore, TeamId.TEAM_1) + ")/("
                + turnTricks(pkScore, TeamId.TEAM_2) + ","
                + turnPoints(pkScore, TeamId.TEAM_2) + ","
                + gamePoints(pkScore, TeamId.TEAM_2) + ")";
    }

}