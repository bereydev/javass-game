package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.bits.Bits32;
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
        currentTrick = pkTrick;
    }

    private long currentScore = PackedScore.INITIAL;
    private long unplayedCards = PackedCardSet.ALL_CARDS;
    private int currentTrick = PackedTrick.INVALID;
    private static final int CARD_SIZE = 6;

    /**
     * @param trump
     * @param score
     * @param firstPlayer
     * @return
     */
    public static TurnState initial(Color trump, Score score, PlayerId firstPlayer) {
        int pkTrick = Bits32.pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE, 0, 4, firstPlayer.ordinal(), 2, trump.ordinal(), 2);
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, pkTrick);
    }

    /**
     * @param pkScore
     * @param pkUnplayedCards
     * @param pkTrick
     * @return
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore) && PackedCardSet.isValid(pkUnplayedCards) && PackedTrick.isValid(pkTrick));
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * @return
     */
    public long packedScore() {
        return currentScore;
    }

    /**
     * @return
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * @return
     */
    public int packedTrick() {
        return currentTrick;
    }

    /**
     * @return
     */
    public Score score() {
        return Score.ofPacked(currentScore);
    }

    /**
     * @return
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * @return
     */
    public Trick trick() {
        return Trick.ofPacked(currentTrick);
    }

    /**
     * @return
     */
    public boolean isTerminal() {
        return trick().isLast();
    }
    
    /**
     * @return
     */
    public PlayerId nextPlayer() {
        return trick().player(trick().index() + 1);
    }
    
    /**
     * @param card
     * @return
     */
    public TurnState withNewCardPlayed(Card card) {
        Preconditions.checkArgument(trick().isFull());
        Trick trick = trick().withAddedCard(card);
        return ofPackedComponents(currentScore, unplayedCards, trick.packed());
    }
    
    /**
     * @return
     */
    public TurnState withTrickCollected() {
        Preconditions.checkArgument(!trick().isFull());
        //TODO pas compris le but ...
        return null;
    }
    
    /**
     * @param card
     * @return
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        Preconditions.checkArgument(trick().isFull());
        TurnState turnState = withNewCardPlayed(card);
        if (trick().isFull()) {
            turnState = turnState.withTrickCollected();
        }
        return turnState;
        
    }

}