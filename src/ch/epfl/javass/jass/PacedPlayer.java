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

    private Player underlyingPlayer;
    private double minTime;

    /**
     * @param underlyingPlayer
     *            The player to be paced.
     * @param minTime
     *            The minimal wait time.
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        double startTime = System.currentTimeMillis();
        double currentTime = System.currentTimeMillis();
        Card card = underlyingPlayer.cardToPlay(state, hand);
        if (currentTime - startTime >= minTime)
            return card;
        try {
            Thread.sleep((int) (minTime + startTime - currentTime) * 1000);
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

}
