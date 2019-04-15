
package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * @author Jonathan Bereyziat and Alexandre Santangelo
 * @date Mar 1, 2019
 */
public final class PackedCardSet {

    /**
     * Private constructor you can't instantiate
     */
    private PackedCardSet() {
    }

    private static final int SIZE = Long.SIZE / 4;
    private static final int COLOR_SIZE = 2;
    private static final int COLOR_START = 4;
    private static final int RANK_SIZE = 4;
    private static final int RANK_START = 0;

    public final static long EMPTY = 0L;
    public final static long ALL_CARDS = Bits64.mask(0, Card.Rank.COUNT)
            | Bits64.mask(SIZE, Card.Rank.COUNT)
            | Bits64.mask(SIZE * 2, Card.Rank.COUNT)
            | Bits64.mask(SIZE * 3, Card.Rank.COUNT);
    private final static long trumpAboveTab[][] = {
            supTrumpCardTab(Card.Color.SPADE),
            supTrumpCardTab(Card.Color.HEART),
            supTrumpCardTab(Card.Color.DIAMOND),
            supTrumpCardTab(Card.Color.CLUB) };
    private final static long colorSetTab[] = { Bits64.mask(0, Card.Rank.COUNT),
            Bits64.mask(SIZE, Card.Rank.COUNT),
            Bits64.mask(SIZE * 2, Card.Rank.COUNT),
            Bits64.mask(3 * SIZE, Card.Rank.COUNT) };

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @return True if the bit chain meets the established criteria
     */
    public static boolean isValid(long pkCardSet) {
        return (pkCardSet & (~ALL_CARDS)) == 0;
    }

    /**
     * @param pkCard
     *            An integer coding for a card
     * @return The set of trump cards that are better than pkCard
     */
    public static long trumpAbove(int pkCard) {
        assert (PackedCard.isValid(pkCard));

        return trumpAboveTab[Bits32.extract(pkCard, COLOR_START,
                COLOR_SIZE)][Bits32.extract(pkCard, RANK_START, RANK_SIZE)];
    }

    /**
     * @param color
     *            The color of the card
     * @return A tab with the sets of cards superior to each card
     */
    private static long[] supTrumpCardTab(Card.Color color) {
        long tab[] = new long[Card.Rank.COUNT];
        
        for(int i=0; i<Card.Rank.COUNT; i++) {
            long set = EMPTY; 
            Card card = Card.of(color, Card.Rank.values()[i]); 
            
            for(int j=0; j<Card.Rank.COUNT; j++) {
                Card card1 = Card.of(color, Card.Rank.values()[j]);
                if(card1.isBetter(color, card))
                    set = add(set,card1.packed()); 
            }
            tab[i]= set; 
        }
        return tab;
    }

    /**
     * @param pkCard
     *            An integer coding for a card
     * @return A singleton containing only pkCard
     */
    public static long singleton(int pkCard) {
        assert (PackedCard.isValid(pkCard));

        return 1L << pkCard;
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @return True if the set is empty
     */
    public static boolean isEmpty(long pkCardSet) {
        assert (isValid(pkCardSet));

        return pkCardSet == EMPTY;
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @return The number of cards in the set
     */
    public static int size(long pkCardSet) {
        return Long.bitCount(pkCardSet);
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @param index
     *            The position of the card in the set
     * @return The packed card version of the card at position index
     */
    public static int get(long pkCardSet, int index) {
        assert (isValid(pkCardSet) && size(pkCardSet) > index);
        for (int i = 0; i < index; i++) {
            pkCardSet = pkCardSet & ~Long.lowestOneBit(pkCardSet);
        }
        return Long.numberOfTrailingZeros(pkCardSet);
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @param pkCard
     *            An integer coding a card
     * @return A set of cards containing all the cards in pkCardSet and the card
     *         pkCard
     */
    public static long add(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet) && PackedCard.isValid(pkCard));

        return pkCardSet | singleton(pkCard);
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @param pkCard
     *            An integer coding a card
     * @return The set of cards pkCardSet without pkCard
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet) && PackedCard.isValid(pkCard));

        return add(pkCardSet, pkCard) - singleton(pkCard);
        // We add the card (no effect if already there)
        // just in case it wasn't there (won't be there in the end anyway).

    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @param pkCard
     *            An integer coding a card
     * @return True if the pkCard is contained in pkCardSet
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        // Assert done in the function
        return add(pkCardSet, pkCard) == pkCardSet;
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @return A set of cards with all the cards that aren't in pkCardSet
     */
    public static long complement(long pkCardSet) {
        assert (isValid(pkCardSet));
        return (~pkCardSet) & ALL_CARDS;
    }

    /**
     * @param pkCardSet1
     *            A long coding a set of cards
     * @param pkCardSet2
     *            A long coding a set of cards
     * @return A set of cards containing all the cards in pkCardSet1 AND/OR in
     *         pkCardSet2;
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 | pkCardSet2;
    }

    /**
     * @param pkCardSet1
     *            A long coding a set of cards
     * @param pkCardSet2
     *            A long coding a set of cards
     * @return A set of cards containing all the cards that are in pkCardSet1
     *         AND pkCardSet2
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1) && isValid(pkCardSet2));
        return pkCardSet1 & pkCardSet2;
    }

    /**
     * @param pkCardSet1
     *            A long coding a set of cards
     * @param pkCardSet2
     *            A long coding a set of cards
     * @return A set with the cards that are in one set but not the other
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1));

        return intersection(pkCardSet1, complement(pkCardSet2));
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @param color
     *            The color of the cards we want
     * @return A subset of pkCardSet with only the cards of color 'color'
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert (isValid(pkCardSet));

        return intersection(pkCardSet, colorSetTab[color.ordinal()]);
    }

    /**
     * @param pkCardSet
     *            A long coding a set of cards
     * @return A string showing the cards contained in pkCardSet
     */
    public static String toString(long pkCardSet) {
        StringJoiner cardList = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkCardSet); i++) {
            int pkCard = get(pkCardSet, i);
            cardList.add(PackedCard.toString(pkCard));
            remove(pkCardSet, pkCard);
        }

        return cardList.toString();
    }
}
