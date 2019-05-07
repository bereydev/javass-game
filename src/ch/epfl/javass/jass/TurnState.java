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

    private final long currentScore;
    private final long unplayedCards;
    private final int currentTrick;

    /**
     * @param trump
     * @param score
     * @param firstPlayer
     * @return the initial state of a jassGame from the trump, score,
     *         firstPlayer given and with an empty first trick
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        int pkTrick = PackedTrick.firstEmpty(trump, firstPlayer);
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS, pkTrick);
    }

    /**
     * @param pkScore
     * @param pkUnplayedCards
     * @param pkTrick
     * @return act like a constructor
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {
        Preconditions.checkArgument(PackedScore.isValid(pkScore)
                && PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick));
        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * @return the pakedScore long value
     */
    public long packedScore() {
        return currentScore;
    }

    /**
     * @return the packedCardset long value
     */
    public long packedUnplayedCards() {
        return unplayedCards;
    }

    /**
     * @return the packedTrick int value
     */
    public int packedTrick() {
        return currentTrick;
    }

    /**
     * @return a Score object
     */
    public Score score() {
        return Score.ofPacked(currentScore);
    }

    /**
     * @return a CardSet object
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(unplayedCards);
    }

    /**
     * @return a Trick object
     */
    public Trick trick() {
        return Trick.ofPacked(currentTrick);
    }

    /**
     * @return true if the State is the last of the game
     */
    public boolean isTerminal() {
        return currentTrick == PackedTrick.INVALID;
    }

    /**
     * the currentTrick must be not full
     * 
     * @return the player that will play the next card in the trick
     */
    public PlayerId nextPlayer() {
        if (PackedTrick.isFull(currentTrick))
            throw new IllegalStateException();
        return PackedTrick.player(currentTrick, PackedTrick.size(currentTrick));
    }

    /**
     * the currentTrick must be not full
     * 
     * @param card
     *            the card to add to the trick
     * @return the state with the new card played
     */
    public TurnState withNewCardPlayed(Card card) {
        if (PackedTrick.isFull(currentTrick))
            throw new IllegalStateException();
        //TODO est-ce qu'il faut lancer une exception ou un assert suffit
        assert (PackedCardSet.contains(unplayedCards, card.packed()));

        int pkTrick = PackedTrick.withAddedCard(currentTrick, card.packed());
        long pkUnplayedCards = PackedCardSet.remove(unplayedCards,
                card.packed());
        return ofPackedComponents(currentScore, pkUnplayedCards, pkTrick);
    }

    /**
     * the currentTrick must be full
     * 
     * @return the new turn state with an empty trick
     */
    public TurnState withTrickCollected() {
        if (!PackedTrick.isFull(currentTrick))
            throw new IllegalStateException();

        long nextScore = PackedScore.withAdditionalTrick(currentScore,
                PackedTrick.winningPlayer(currentTrick).team(),
                PackedTrick.points(currentTrick));
        int nextTrick = PackedTrick.nextEmpty(currentTrick);
        return new TurnState(nextScore, unplayedCards, nextTrick);
    }

    /**
     * the currentTrick must be not full
     * 
     * @param card
     *            the card to add to the trick
     * @return the new turn state eventually with an empty trick if the card
     *         played fulfill the trick
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        if (PackedTrick.isFull(currentTrick))
            throw new IllegalStateException();

        TurnState turnState = withNewCardPlayed(card);
        if (PackedTrick.isFull(turnState.currentTrick))
            turnState = turnState.withTrickCollected();
        return turnState;

    }

}
