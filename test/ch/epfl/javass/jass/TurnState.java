/**
 * 
 */
package ch.epfl.javass.jass;

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
        
        return new TurnState(score, PackedCardSet.ALL_CARDS, pkTrick)
    }

}
