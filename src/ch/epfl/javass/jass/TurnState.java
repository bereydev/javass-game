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
        currentScore = pkScore;
        unplayedCards = pkCardSet;
        currentTrick = pkTrick;
    }
    public TurnState(TurnState toCopy) {
        currentScore = toCopy.currentScore; 
        unplayedCards = toCopy.unplayedCards; 
        currentTrick = toCopy.currentTrick; 
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
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        int pkTrick = Bits32.pack(PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE, 0, 4, firstPlayer.ordinal(), 2,
                trump.ordinal(), 2);
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
        Preconditions.checkArgument(PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick));
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
        return packedTrick() == PackedTrick.INVALID;
    }

    /**
     * @return
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(currentTrick)) {
            throw new IllegalStateException();
        }
        return PackedTrick.player(currentTrick, PackedTrick.size(currentTrick));
    }

    /**
     * @param card
     * @return
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(currentTrick)) {
            throw new IllegalStateException();
        }
        Trick trick = trick().withAddedCard(card);
        CardSet unplayedCards = unplayedCards().remove(card);
        return ofPackedComponents(currentScore, unplayedCards.packed(),
                trick.packed());
    }

    /**
     * @return
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(currentTrick)) {
            throw new IllegalStateException();
        }
        Score nextScore = score().withAdditionalTrick(
                trick().winningPlayer().team(), trick().points());
        Trick nextTrick = trick().nextEmpty();
        return new TurnState(nextScore.packed(), unplayedCards,
                nextTrick.packed());
    }

    /**
     * @param card
     * @return
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if (trick().isFull()) {
            throw new IllegalStateException();
        }
        TurnState turnState = withNewCardPlayed(card);
        if (turnState.trick().isFull()) {
            turnState = turnState.withTrickCollected();
        }
        return turnState;

    }

}
