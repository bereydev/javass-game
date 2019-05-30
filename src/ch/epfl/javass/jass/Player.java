package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.gui.MessageId;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author astra
 *
 */
public interface Player {
    /**
     * @param state
     * @param hand
     * @return The card that the player wan't to play knowing that the current
     *         turn is the one described by state and that the player has the
     *         cards hand in the hand
     */
    abstract Card cardToPlay(TurnState state, CardSet hand);
    
    abstract Color trumpToPlay(CardSet hand); 

    default public void setPlayers(PlayerId ownId,
            Map<PlayerId, String> playerNames) {
    }

    /**
     * @param newHand
     *            The new hand of the player
     * 
     *            Updates the hand of the player with newHand
     */
    default void updateHand(CardSet newHand) {

    }

    /**
     * @param trump
     *            The trump color of the trick.
     * 
     *            Informs the player of the current trump color.
     */
    default void setTrump(Color trump) {

    }

    /**
     * @param newTrick
     *            The current Trick.
     * 
     *            Updates the trick of the player to newTrick.
     */
    default void updateTrick(Trick newTrick) {

    }

    /**
     * @param score
     *            The current score
     * 
     *            Updates the score of the player to 'score'.
     */
    default void updateScore(Score score) {

    }

    /**
     * @param winningTeam
     *            The team whose score is superior or equal to 1000
     * 
     *            Informs the player of the winningTeam
     */
    default void setWinningTeam(TeamId winningTeam) {

    }
    
}
