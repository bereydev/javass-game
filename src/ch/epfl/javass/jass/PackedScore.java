
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

    public static final long INITIAL = 0L;
    private static final int ZERO = 0;
    private static final int TURN_TRICK_SIZE = 4;
    private static final int TURN_POINTS_SIZE = 9;
    private static final int SCORE_SIZE = 24;
    private static final int GAME_POINTS_SIZE = 11;
    private static final int GAME_POINTS_START = 13;
    private static final int MAX_TRICKS = 9;
    private static final int MAX_GAME_POINTS = 2000;
    private static final int MAX_TURN_POINTS = 257;

    /**
     * @param pkScore
     *            the Long that represent the score informations about the two
     *            teams
     * @return true if the pkScore is valid and false otherwise
     */
    public static boolean isValid(long pkScore) {
        long score1 = Bits64.extract(pkScore, ZERO, Integer.SIZE);
        long score2 = Bits64.extract(pkScore, Integer.SIZE, Integer.SIZE);
        long turnTricks1 = Bits64.extract(score1, ZERO, TURN_TRICK_SIZE);
        long turnPoints1 = Bits64.extract(score1, TURN_TRICK_SIZE,
                TURN_POINTS_SIZE);
        long gamePoints1 = Bits64.extract(score1, GAME_POINTS_START,
                GAME_POINTS_SIZE);
        long rest_1 = Bits64.extract(score1, SCORE_SIZE,
                Integer.SIZE - SCORE_SIZE);
        long turnTricks2 = Bits64.extract(score2, ZERO, TURN_TRICK_SIZE);
        long turnPoints2 = Bits64.extract(score2, TURN_TRICK_SIZE,
                TURN_POINTS_SIZE);
        long gamePoints2 = Bits64.extract(score2, GAME_POINTS_START,
                GAME_POINTS_SIZE);
        long rest_2 = Bits64.extract(score2, SCORE_SIZE,
                Integer.SIZE - SCORE_SIZE);
        return (turnTricks1 >= ZERO && turnTricks1 <= MAX_TRICKS)
                && (turnPoints1 >= ZERO && turnPoints1 <= MAX_TURN_POINTS)
                && (gamePoints1 >= ZERO && gamePoints1 <= MAX_GAME_POINTS)
                && (rest_1 == ZERO)
                && (turnTricks2 >= ZERO && turnTricks2 <= MAX_TRICKS)
                && (turnPoints2 >= ZERO && turnPoints2 <= MAX_TURN_POINTS)
                && (gamePoints2 >= ZERO && gamePoints2 <= MAX_GAME_POINTS)
                && (rest_2 == ZERO);
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
        long b1 = Bits64.pack(turnTricks1, TURN_TRICK_SIZE, turnPoints1,
                TURN_POINTS_SIZE);
        long score1 = Bits64.pack(b1, GAME_POINTS_START, gamePoints1,
                GAME_POINTS_SIZE);
        long b2 = Bits64.pack(turnTricks2, TURN_TRICK_SIZE, turnPoints2,
                TURN_POINTS_SIZE);
        long score2 = Bits64.pack(b2, GAME_POINTS_START, gamePoints2,
                GAME_POINTS_SIZE);
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
            return (int) Bits64.extract(pkScore, ZERO, TURN_TRICK_SIZE);
        } else {
            return (int) Bits64.extract(pkScore, Integer.SIZE, TURN_TRICK_SIZE);
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
            return (int) Bits64.extract(pkScore, TURN_TRICK_SIZE,
                    TURN_POINTS_SIZE);
        } else {
            return (int) Bits64.extract(pkScore, Integer.SIZE + TURN_TRICK_SIZE,
                    TURN_POINTS_SIZE);
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
            return (int) Bits64.extract(pkScore, GAME_POINTS_START,
                    GAME_POINTS_SIZE);
        } else {
            return (int) Bits64.extract(pkScore,
                    Integer.SIZE + GAME_POINTS_START, GAME_POINTS_SIZE);
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

        return pack(ZERO, ZERO, gamePoints1, ZERO, ZERO, gamePoints2);
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