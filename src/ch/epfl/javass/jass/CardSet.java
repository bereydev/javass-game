package ch.epfl.javass.jass;

import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * @author Jonathan Bereyziat
 *
 */
public final class CardSet {

    private final long pkCardSet;

    /**
     * private constructor, can't instantiate
     */
    private CardSet(long pkCards) {
        pkCardSet = pkCards;
    }

    public static final CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);
    public static final CardSet ALL_CARDS = new CardSet(
            PackedCardSet.ALL_CARDS);

    /**
     * @param cards
     *            List of Card object you want to create a set of
     * @return A new CardSet
     */
    public static CardSet of(List<Card> cards) {
        CardSet cardSet = EMPTY;
        for (Card card : cards)
            cardSet = cardSet.add(card);
        return cardSet;
    }

    /**
     * @param packed
     *            Packed version of the set you want to create
     * @return A new CardSet
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        return new CardSet(packed);
    }

    /**
     * @return the packed version of the CardSet
     */
    public long packed() {
        return pkCardSet;
    }

    /**
     * @return True if there is no card in the CardSet
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(pkCardSet);
    }

    /**
     * @return The number of card in the CardSet
     */
    public int size() {
        return PackedCardSet.size(pkCardSet);
    }

    /**
     * @param index
     *            the position in the game of the card you wan't to get (0 is
     *            the first card of the game if the CardSet is not empty)
     * @return
     */
    public Card get(int index) {
        return Card.ofPacked(PackedCardSet.get(pkCardSet, index));
    }

    /**
     * @param card
     *            the Card object you want to add
     * @return the CardSet with the added Card
     */
    public CardSet add(Card card) {
        return ofPacked(PackedCardSet.add(pkCardSet, card.packed()));
    }

    /**
     * @param card
     *            the Card you want to remove
     * @return the CardSet with the removed Card
     */
    public CardSet remove(Card card) {
        return ofPacked(PackedCardSet.remove(pkCardSet, card.packed()));
    }

    /**
     * @param card
     *            A Card object
     * @return True if the card is in the CardSet
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(pkCardSet, card.packed());
    }

    /**
     * @return the complement of the CardSet
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(pkCardSet));
    }

    /**
     * @param that
     *            a CardSet object
     * @return the logical union of that and the CardSet
     */
    public CardSet union(CardSet that) {
        return ofPacked(PackedCardSet.union(pkCardSet, that.packed()));
    }

    /**
     * @param that
     *            a CardSet object
     * @return the logical intersection of that and the CardSet
     */
    public CardSet intersection(CardSet that) {
        return ofPacked(PackedCardSet.intersection(pkCardSet, that.packed()));
    }

    /**
     * @param that
     *            a CardSet object
     * @return the difference between the CardSet and that
     */
    public CardSet difference(CardSet that) {
        return ofPacked(PackedCardSet.difference(pkCardSet, that.packed()));
    }

    /**
     * @param color
     *            a Color object (color of the cards you want to get)
     * @return the subset of the color "color"
     */
    public CardSet subsetOfColor(Card.Color color) {
        return ofPacked(PackedCardSet.subsetOfColor(pkCardSet, color));
    }

    @Override
    public int hashCode() {
        return Long.hashCode(pkCardSet);
    }

    @Override
    public String toString() {
        return PackedCardSet.toString(pkCardSet);
    }

    @Override
    public boolean equals(Object other) {
        return other instanceof CardSet && ((CardSet) other).pkCardSet == pkCardSet;
    }

}
