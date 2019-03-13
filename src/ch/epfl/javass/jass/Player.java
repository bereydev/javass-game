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
    public abstract Card cardToPlay(TurnState state, CardSet hand);
    //TODO à mettre en abstact ou non ?
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames);
    public void updateHand(CardSet newHand);
    public void setTrump(Color trump);
    public void updateTrick(Trick newTrick);
    public void updateScore(Score score);
    public void setWinningTeam(TeamId winningTeam);
}
