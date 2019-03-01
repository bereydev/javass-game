/*
 *	Author : Alexandre Santangelo 

 *	Date   : Mar 1, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

public final class PackedCardSet {

    private final static int NUM_OF_CARDS = 9;
    public final static long EMPTY = 0;
    public final static long ALL_CARDS = Bits64.mask(0, 9) | Bits64.mask(16, 9)
            | Bits64.mask(32, 9) | Bits64.mask(48, 9);
    private final static long trumpAboveTab[][] = {
            supCardTab(Card.Color.SPADE), supCardTab(Card.Color.HEART),
            supCardTab(Card.Color.DIAMOND), supCardTab(Card.Color.CLUB) };
    private final static long colorSetTab[] = { Bits64.mask(0, 9),
            Bits64.mask(16, 9), Bits64.mask(32, 9), Bits64.mask(48, 9) };

    /**
     * @param pkCardSet A long coding a set of cards 
     * @return  True if the bit chain meets the established criteria 
     */
    public boolean isValid(long pkCardSet) {
        return (pkCardSet & (~ALL_CARDS)) == 0;
    }

    /**
     * @param pkCard An integer coding for a card 
     * @return  The set of cards that are better than pkCard 
     */
    public long trumpAbove(int pkCard) {
        assert (PackedCard.isValid(pkCard));

        return trumpAboveTab[Bits32.extract(pkCard, 4, 2)][Bits32
                .extract(pkCard, 0, 4)];
    }

    /**
     * @param color The color of the card 
     * @return  A tab with the sets of cards superior to each card 
     */
    private static long[] supCardTab(Card.Color color) {
        long JACK = 1L << 5;
        long NINE = 1L << 3;
        long tab[] = new long[9];
        tab[Card.Rank.SIX.ordinal()] = Bits64.mask(1,
                NUM_OF_CARDS - 1) << color.ordinal() * 16;
        tab[Card.Rank.SEVEN.ordinal()] = Bits64.mask(2,
                NUM_OF_CARDS - 2) << color.ordinal() * 16;
        tab[Card.Rank.EIGHT.ordinal()] = Bits64.mask(3,
                NUM_OF_CARDS - 3) << color.ordinal() * 16;
        tab[Card.Rank.NINE.ordinal()] = JACK << color.ordinal() * 16;
        tab[Card.Rank.TEN.ordinal()] = Bits64.mask(5, NUM_OF_CARDS - 5)
                + NINE << color.ordinal() * 16;
        tab[Card.Rank.JACK.ordinal()] = 0 << color.ordinal() * 16;
        tab[Card.Rank.QUEEN.ordinal()] = Bits64.mask(7, NUM_OF_CARDS - 7) + NINE
                + JACK << color.ordinal() * 16;
        tab[Card.Rank.KING.ordinal()] = NINE + JACK << color.ordinal() * 16;

        return tab;
    }

    /**
     * @param pkCard    An integer coding for a card 
     * @return  A singleton containing only pkCard 
     */
    public long singleton(int pkCard) {
        assert (PackedCard.isValid(pkCard));
        long rank = (long) Bits32.extract(pkCard, 0, 4);

        return rank << Bits32.extract(pkCard, 4, 2) * 16;
    }

    /**
     * @param pkCardSet A long coding a set of cards 
     * @return  True if the set is empty 
     */
    public boolean isEmpty(long pkCardSet) {
        assert (isValid(pkCardSet));

        return pkCardSet == 0;
    }

    /**
     * @param pkCardSet A long coding a set of cards 
     * @return  The number of cards in the set 
     */
    public int size(long pkCardSet) {
        return Long.bitCount(pkCardSet);
    }

    /**
     * @param pkCardSet A long coding a set of cards 
     * @param index The position of the card in the set 
     * @return  The packed card version of the card at position index 
     */
    public int get(long pkCardSet, int index) {
        // TODO : Je suis vraiment pas sur, ça m'a l'air plus simple, je ne
        // suis pas sur de comprendre l'énoncé.
        int count = 0;
        while (index - 16 >= 0) {
            index -= 16;
            count++;
        }
        return Bits32.pack(index, 4, count, 2);
    }

    /**
     * @param pkCardSet A long coding a set of cards 
     * @param pkCard    An integer coding a card 
     * @return  A set of cards containing all the cards in pkCardSet and the card pkCard
     */
    public long add(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet));

        return pkCardSet | singleton(pkCard);
    }

    /**
     * @param pkCardSet A long coding a set of cards 
     * @param pkCard An integer coding a card 
     * @return  The set of cards pkCardSet without pkCard 
     */
    public long remove(long pkCardSet, int pkCard) {
        assert (isValid(pkCardSet));

        return add(pkCardSet, pkCard) - singleton(pkCard);
        // We add the card (no effect if already there)
        // just in case it wasn't there (won't be there in the end anyway).

    }

    /**
     * @param pkCardSet A long coding a set of cards
     * @param pkCard    An integer coding a card 
     * @return  True if the pkCard is contained in pkCardSet 
     */
    public boolean contains(long pkCardSet, int pkCard) {
        // Assert done in the function
        return add(pkCardSet, pkCard) == pkCardSet;
    }

    /**
     * @param pkCardSet A long coding a set of cards
     * @return  A set of cards with all the cards that aren't in pkCardSet 
     */
    public long complement(long pkCardSet) {
        assert (isValid(pkCardSet));
        // TODO : ça devrais le faire, nan ?
        return (~pkCardSet) & ALL_CARDS;
    }

    /**
     * @param pkCardSet1    A long coding a set of cards
     * @param pkCardSet2    A long coding a set of cards
     * @return  A set of cards containing all the cards in pkCardSet1 AND/OR in pkCardSet2; 
     */
    public long union(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 | pkCardSet2;
    }

    /**
     * @param pkCardSet1    A long coding a set of cards
     * @param pkCardSet2    A long coding a set of cards
     * @return  A set of cards containing all the cards that are in pkCardSet1 AND pkCardSet2
     */
    public long intersection(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1) && isValid(pkCardSet2));

        return pkCardSet1 & pkCardSet2;
    }

    /**
     * @param pkCardSet1    A long coding a set of cards
     * @param pkCardSet2    A long coding a set of cards
     * @return  A set with the cards that are in one set but not the other 
     */
    public long difference(long pkCardSet1, long pkCardSet2) {
        assert (isValid(pkCardSet1));
        pkCardSet2 = complement(pkCardSet2);

        return pkCardSet1 & pkCardSet2;
    }

    /**
     * @param pkCardSet A long coding a set of cards
     * @param color The color of the cards we want 
     * @return  A subset of pkCardSet with only the cards of color 'color' 
     */
    public long subsetOfColor(long pkCardSet, Card.Color color) {
        assert (isValid(pkCardSet));

        return pkCardSet & colorSetTab[color.ordinal()];
    }
    /**
     * @param pkCardSet A long coding a set of cards
     * @return  A string showing the cards contained in pkCardSet 
     */
    public String toString(long pkCardSet) {
        //TODO 
        return "Seems complicated"; 
    }
}
