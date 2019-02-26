
package ch.epfl.javass.jass;


public final class PackedScore {

    public static final long INITIAL = 0;

    /**
     * @param pkScore
     * @return
     */
    public static boolean isValid(long pkScore) {
        return true;
    }

    /**
     * @param turnTricks1
     * @param turnPoints1
     * @param gamePoints1
     * @param turnTricks2
     * @param turnPoints2
     * @param gamePoints2
     * @return
     */
   public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {

        return 1L;
    }

    /**
     * @param pkScore
     * @param t
     * @return
     */
    public static int turnTricks(long pkScore, TeamId t) {
        return 0;

    }

    /**
     * @param pkScore
     * @param t
     * @return
     */
    public static int turnPoints(long pkScore, TeamId t) {
        return 0;
    }

    /**
     * @param pkScore
     * @param t
     * @return
     */
    public static int gamePoints(long pkScore, TeamId t) {
        return 0;
    }

    /**
     * @param pkScore
     * @param t
     * @return
     */
    public static int totalPoints(long pkScore, TeamId t) {
        return 0;
    }

    /**
     * @param pkScore
     * @param winningTeam
     * @param trickPoints
     * @return
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        return 0;
    }

    /**
     * @param pkScore
     * @return
     */
    public static long nextTurn(long pkScore) {
        return 0;
    }

    /**
     * @param pkScore
     * @return
     */
    public static String toString(long pkScore) {
        return "Hello Jon";
    }

}