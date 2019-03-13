/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

/**
 * @author Jonathan Bereyziat
 *
 */
public final class TurnState {

    /**
     * Private constructor
     */
    private TurnState(long pkScore, long pkCardSet, int pkTrick) {
        currentScore = pkCardSet;
        unplayedCards = pkCardSet;
        currentScore = pkTrick;
    }
    
    private long currentScore = PackedScore.INITIAL;
    private long unplayedCards = PackedCardSet.ALL_CARDS;
    private int currentTrick = PackedTrick.INVALID;
    
    
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        //TODO : I PUT A ZERO BECAUSE THERE IS A BUG HERE ! 
        return new TurnState(0, PackedCardSet.ALL_CARDS, 0); 
    }

}
