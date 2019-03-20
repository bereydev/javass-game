package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public interface Player {
    /**
     * @param state
     * @param hand
     * @return The card that the player wan't to play knowing that the current
     *         turn is the one described by state and that the player has the
     *         cards hand in the hand
     */
    abstract Card cardToPlay(TurnState state, CardSet hand);
    default public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    }
    default void updateHand(CardSet newHand) {
        
    }
    default void setTrump(Color trump) {
        
    }
    default void updateTrick(Trick newTrick) {
        
    }
    default void updateScore(Score score) {
        
    }
    default void setWinningTeam(TeamId winningTeam) {
        
    }
}
