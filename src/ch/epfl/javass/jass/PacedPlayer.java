/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 13, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PacedPlayer implements Player {

    private final Player underlyingPlayer;
    private final long minTime;

    private static final int MILLI_SECONDS = 1000;

    /**
     * @param underlyingPlayer
     *            The player to be paced.
     * @param minTime
     *            The minimal wait time.
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        assert (minTime >= 0);
        this.underlyingPlayer = underlyingPlayer;
        //in miliseconds
        this.minTime = (long)minTime * MILLI_SECONDS;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long startTime = System.currentTimeMillis();
        Card card = underlyingPlayer.cardToPlay(state, hand);
        long currentTime = System.currentTimeMillis();
        if (currentTime - startTime >= minTime)
            return card;
        try {
            Thread.sleep((minTime - (currentTime - startTime)));
        } catch (InterruptedException e) {
            /* do nothing */ }
        return card;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);

    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);

    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#trumpToPlay(ch.epfl.javass.jass.Card.Color, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color trumpToPlay(CardSet hand) {
        return underlyingPlayer.trumpToPlay(hand);
    }

}
