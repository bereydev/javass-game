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
    private static final int CARD_SIZE = 6;
    private static final int TRICKS_BY_TURN = 8;

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick is correctly packed.
     */
    public static boolean isValid(int pkTrick) {
        
        boolean aCardIsInvalid = true;
        
        for (int i = 0; i <4 ; i++) {
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
        int index = Bits32.extract(pkTrick, 24, 4);

        return index <= 8 && index >= 0;
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
                CARD_SIZE, 0, 4, firstPlayer.ordinal(), 2, trump.ordinal(), 2);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The next trick, with no cards
     */
    public static int nextEmpty(int pkTrick) {

        assert(isValid(pkTrick)); 
        if (isLast(pkTrick))
            return INVALID;
        return Bits32.pack(PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID,
                CARD_SIZE, Bits32.extract(pkTrick, 24, 4) + 1, 4,
                winningPlayer(pkTrick).ordinal(), 2,
                Bits32.extract(pkTrick, 30, 2), 2);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if this is the last trick of the turn
     */
    public static boolean isLast(int pkTrick) {
        assert (isValid(pkTrick));
        return Bits32.extract(pkTrick, 24, 4) == TRICKS_BY_TURN;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick has no cards.
     */
    public static boolean isEmpty(int pkTrick) {
        assert (isValid(pkTrick));
        for (int i = 0; i < 4; i++) {
            if (Bits32.extract(pkTrick, CARD_SIZE * i,
                    CARD_SIZE) != PackedCard.INVALID)
                return false;
        }
        return true;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick has all 4 cards.
     */
    public static boolean isFull(int pkTrick) {
        assert (isValid(pkTrick));
        return size(pkTrick) == 4;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The number of cards in the trick.
     */
    public static int size(int pkTrick) {
        assert (isValid(pkTrick));
        int nbrOfCards = 0;
        for (int i = 0; i < 4; i++) {
            if (Bits32.extract(pkTrick, CARD_SIZE * i, CARD_SIZE) != PackedCard.INVALID)
                nbrOfCards += 1;
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
        return Card.Color.values()[Bits32.extract(pkTrick, 30, 2)];
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param index
     * @return
     */
    public static PlayerId player(int pkTrick, int index) {
        assert (isValid(pkTrick));
        // a checker si c'est bien ça
        return PlayerId.values()[(Bits32.extract(pkTrick, 28, 2) + index)%4];
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The index of the trick
     */
    public static int index(int pkTrick) {
        assert (isValid(pkTrick));
        return Bits32.extract(pkTrick, 24, 4);
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @param index
     *            The index of the card (0,1,2 or 3)
     * @return The pkCard at that index
     */
    public static int card(int pkTrick, int index) {
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
        long playableCardSet = PackedCardSet.EMPTY;
        long trickColorInHand = PackedCardSet.subsetOfColor(pkHand,
                baseColor(pkTrick));
        if (baseColor(pkTrick) == trump(pkTrick)) {
            if (PackedCardSet.size(trickColorInHand) == 0 || (PackedCardSet
                    .size(trickColorInHand) == 1
                    && PackedCardSet.contains(trickColorInHand,
                            PackedCard.pack(trump(pkTrick), Rank.JACK)))) {
                playableCardSet = pkHand;
            } else {
                playableCardSet = trickColorInHand;
            }
        } else {
            if (PackedCardSet.size(trickColorInHand) == 0) {
                if (PackedCard.color(winningCard(pkTrick)) == trump(pkTrick)) {
                    playableCardSet = PackedCardSet.union(
                            PackedCardSet.difference(pkHand,
                                    PackedCardSet.subsetOfColor(pkHand,
                                            trump(pkTrick))),
                            PackedCardSet.trumpAbove(winningCard(pkTrick)));
                } else {
                    playableCardSet = pkHand;
                }
            } else {
                if (PackedCard.color(winningCard(pkTrick)) == trump(pkTrick)) {
                    playableCardSet = PackedCardSet.union(trickColorInHand,
                            PackedCardSet.trumpAbove(winningCard(pkTrick)));
                } else {
                    playableCardSet = PackedCardSet.union(trickColorInHand, PackedCardSet.subsetOfColor(pkHand,
                            trump(pkTrick)));
                }
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
        int winningCard = card(pkTrick, 0);
        for (int i = 0; i < size(pkTrick); i++) {
            int pkCard = card(pkTrick, i);
            if (PackedCard.isBetter(trump(pkTrick), pkCard, winningCard)) {
                winningCard = pkCard;
            }
        }
        return winningCard;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The points of the trick
     */
    public static int points(int pkTrick) {
        assert(isValid(pkTrick)); 
        int total = 0;
        Color trump = trump(pkTrick);
        for (int i = 0; i <size(pkTrick) ; i++) {
            total += PackedCard.points(trump,
                    Bits32.extract(pkTrick, i * CARD_SIZE, CARD_SIZE));
        }
        if (isLast(pkTrick))
            total += 5;

        return total;
    }

    /**
     * @param pkTrick
     *            A packed trick.
     * @return The winning player of the trick
     */
    public static PlayerId winningPlayer(int pkTrick) {

//        int winningCard = Bits32.extract(pkTrick, 0, CARD_SIZE);
//        int winningPosition = 0;
//        for (int i = 0; i < size(pkTrick); i++) {
//            int pkCard = card(pkTrick, i);
//            if (PackedCard.isBetter(trump(pkTrick), pkCard, winningCard)) {
//                winningCard = pkCard;
//                winningPosition++;
//            }
//        }
       // return player(pkTrick, winningPosition);
        assert(isValid(pkTrick)); 
        int winningCard = winningCard(pkTrick); 
        int index =0; 
        for(int i=0; i<size(pkTrick); i++) {
            if(Bits32.extract(pkTrick, i,CARD_SIZE*i)==winningCard) {
                index = i; 
            }
        }
        return PlayerId.values()[(Bits32.extract(pkTrick, 28, 2) + index)%4];
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
