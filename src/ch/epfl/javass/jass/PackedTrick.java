/*
 *  Author : Alexandre Santangelo and Jonathan Bereyziat
 *  Date   : Mar 4, 2019    
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.StringJoiner;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public final class PackedTrick {

    private PackedTrick() {
        // not-instantiable class.
    }

    public static final int INVALID = ~0;
    private static final int SINGLE = 1;
    private static final int CARD_SIZE = 6;
    private static final int NUMBER_OF_CARDS = 4;
    private static final int INDEX_START = 24;
    private static final int INDEX_SIZE = 4;
    private static final int MAX_INDEX = 8;
    private static final int ZERO = 0;
    private static final int PLAYER_START = 28;
    private static final int PLAYER_SIZE = 2;
    private static final int TRUMP_START = 30;
    private static final int TRUMP_SIZE = 2;

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick is correctly packed.
     */
    public static boolean isValid(int pkTrick) {

        boolean aCardIsInvalid = true;

        for (int i = 0; i < NUMBER_OF_CARDS; i++) {
            int card = Bits32.extract(pkTrick, CARD_SIZE * i, CARD_SIZE);

            // VALID CARD CASE
            if (card != PackedCard.INVALID) {
                if (!aCardIsInvalid)
                    return false;
            }
            // INVALID CARD CASE
            else
                aCardIsInvalid = false;
        }
        int index = Bits32.extract(pkTrick, INDEX_START, INDEX_SIZE);

        return index <= MAX_INDEX && index >= ZERO;
    }

    /**
     * @param trump
     *            The trump color of the trick
     * @param firstPlayer
     *            The given player
     * @return
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {

        return Bits32.pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, ZERO, INDEX_SIZE, firstPlayer.ordinal(), PLAYER_SIZE,
                trump.ordinal(), TRUMP_SIZE);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The next trick, with no cards
     */
    public static int nextEmpty(int pkTrick) {
        assert (isValid(pkTrick));
        if (isLast(pkTrick))
            return INVALID;

        return Bits32.pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, Bits32.extract(pkTrick, INDEX_START, INDEX_SIZE) + 1,
                INDEX_SIZE, winningPlayer(pkTrick).ordinal(), PLAYER_SIZE,
                Bits32.extract(pkTrick, TRUMP_START, TRUMP_SIZE), TRUMP_SIZE);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if this is the last trick of the turn
     */
    public static boolean isLast(int pkTrick) {
        assert (isValid(pkTrick));
        return index(pkTrick) == Jass.TRICKS_PER_TURN - 1;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick has no cards.
     */
    public static boolean isEmpty(int pkTrick) {
        assert (isValid(pkTrick));
        //since the trick has to be valid the trick is empty if the first card is invalid
        return card(pkTrick, 0) == PackedCard.INVALID;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick has all 4 cards.
     */
    public static boolean isFull(int pkTrick) {
        assert (isValid(pkTrick));
      //since the trick has to be valid the trick is full if the last card is not invalid
        return card(pkTrick, 3) != PackedCard.INVALID;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The number of cards in the trick.
     */
    public static int size(int pkTrick) {
        assert (isValid(pkTrick));
        int nbrOfCards = 0;
        for (int i = 0; i < NUMBER_OF_CARDS; i++) {
            if (card(pkTrick, i) != PackedCard.INVALID)
                nbrOfCards++;
        }
        return nbrOfCards;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The color of the trump
     */
    public static Color trump(int pkTrick) {
        assert (isValid(pkTrick));
        return Card.Color.values()[Bits32.extract(pkTrick, TRUMP_START,
                TRUMP_SIZE)];
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param index
     *            Of the player you want
     * @return
     */
    public static PlayerId player(int pkTrick, int index) {
        assert (index >= 0 &&  index < PlayerId.COUNT);
        assert (isValid(pkTrick));
        return PlayerId
                .values()[(Bits32.extract(pkTrick, PLAYER_START, PLAYER_SIZE)
                        + index) % PlayerId.COUNT];
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The index of the trick
     */
    public static int index(int pkTrick) {
        assert (isValid(pkTrick));
        return Bits32.extract(pkTrick, INDEX_START, INDEX_SIZE);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param index
     *            The index of the card (0,1,2 or 3)
     * @return The pkCard at that index
     */
    public static int card(int pkTrick, int index) {
        assert (index >= 0 &&  index < NUMBER_OF_CARDS);
        assert (isValid(pkTrick));
        // HERE I SUPPOSE THAT THE INDEX IS BETWEEN 0 AND 3
        return Bits32.extract(pkTrick, index * CARD_SIZE, CARD_SIZE);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param pkCard
     *            A packed card.
     * @return pkTrick with an added card, pkCard.
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert (isValid(pkTrick) && PackedCard.isValid(pkCard));
        pkCard = ~(Bits32.mask(CARD_SIZE * size(pkTrick), CARD_SIZE))
                | (pkCard << CARD_SIZE * size(pkTrick));
        pkTrick &= pkCard;
        return pkTrick & pkCard;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The color of the card that was first played
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);
        return Card.Color.values()[Bits32.extract(pkTrick, 4, 2)];
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param pkHand
     *            A packed hand of cards
     * @return A collection of all the playable cards
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert (isValid(pkTrick) && PackedCardSet.isValid(pkHand));
        if (isEmpty(pkTrick))
            return pkHand;

        Color baseColor = baseColor(pkTrick);
        Color trumpColor = trump(pkTrick);
        boolean trumpTrick = (baseColor == trumpColor);
        boolean isCut = (PackedCard.color(winningCard(pkTrick)) == trumpColor
                && !trumpTrick);
        long playableCardSet = pkHand;
        long baseSet = PackedCardSet.subsetOfColor(pkHand, baseColor);
        long trumpSet = PackedCardSet.subsetOfColor(pkHand, trumpColor);

        long trumpAboveSet = PackedCardSet.intersection(pkHand,
                PackedCardSet.trumpAbove(winningCard(pkTrick)));

        if (baseSet != 0L) {
            playableCardSet = PackedCardSet.union(baseSet, trumpSet);
            if (trumpTrick && PackedCardSet.size(baseSet) == SINGLE
                    && PackedCardSet.contains(baseSet,
                            PackedCard.pack(trumpColor, Rank.JACK))) {
                playableCardSet = pkHand;
            }
            if (isCut) {
                playableCardSet = PackedCardSet.union(baseSet, trumpAboveSet);
            }
        } else {
            if (!(trumpSet == 0L)) {
                playableCardSet = PackedCardSet.EMPTY;
                if (isCut) {
                    if (pkHand == trumpSet && trumpAboveSet == 0L) {
                        return pkHand;
                    }
                    for (int i = 0; i < 4; i++) {
                        if (Card.Color.values()[i] != trumpColor) {
                            playableCardSet = PackedCardSet.union(
                                    playableCardSet,
                                    PackedCardSet.subsetOfColor(pkHand,
                                            Card.Color.values()[i]));
                        }
                    }
                    playableCardSet = PackedCardSet.union(playableCardSet,
                            trumpAboveSet);
                } else {
                    playableCardSet = pkHand;
                }
            } else {
                playableCardSet = pkHand;
            }
        }
        return playableCardSet;
    }

    /**
     * @param pkTrick
     * @return the winning card of the pkTrick
     */
    private static int winningCard(int pkTrick) {
        assert (isValid(pkTrick));
        int winningCard = card(pkTrick, ZERO);
        for (int i = 0; i < size(pkTrick); i++) {
            int pkCard = card(pkTrick, i);
            if (PackedCard.isBetter(trump(pkTrick), pkCard, winningCard))
                winningCard = pkCard;
        }
        if (PackedCard.isValid(winningCard))
            return winningCard;
        return 0;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The points of the trick
     */
    public static int points(int pkTrick) {
        assert (isValid(pkTrick));
        int total = 0;
        Color trump = trump(pkTrick);
        for (int i = 0; i < size(pkTrick); i++)
            total += PackedCard.points(trump, card(pkTrick, i));

        if (isLast(pkTrick))
            total += Jass.LAST_TRICK_ADDITIONAL_POINTS;

        return total;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The winning player of the trick
     */
    public static PlayerId winningPlayer(int pkTrick) {

        assert isValid(pkTrick);
        int winningCard = winningCard(pkTrick);
        int index = ZERO;
        for (int i = 0; i < size(pkTrick); i++) {
            if (card(pkTrick, i) == winningCard)
                index = i;
        }
        return player(pkTrick, index);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The string version of the trick
     */
    public static String toString(int pkTrick) {
        StringJoiner cardList = new StringJoiner(",", "{", "}");
        for (int i = 0; i < size(pkTrick); i++) {
            int pkCard = card(pkTrick, i);
            cardList.add(PackedCard.toString(pkCard));
        }
        return cardList.toString();
    }

}
