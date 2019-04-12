package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * @author : Alexandre Santangelo Date : Feb 19, 2019
 */
public class PackedCard {

    /**
     * Private constructor you can't instantiate
     */
    private PackedCard() {
    }

    public static int INVALID = 63;
    private static final int RANK_SIZE = 4;
    private static final int RANK_START = 0;
    private static final int COLOR_SIZE = 2;
    private static final int COLOR_START = 4;
    private static final int MAX_RANK = 8;
    private static final int MAX_COLOR = 3;
    private static final int ZERO = 0;

    /**
     * @param pkCard
     *            A card in packed representation
     * @return true if the packed card corresponds to a card
     */
    public static boolean isValid(int pkCard) {
        int rank = Bits32.extract(pkCard, RANK_START, RANK_SIZE);
        int color = Bits32.extract(pkCard, COLOR_START, COLOR_SIZE);
        // bits that should be 0
        int rest = Bits32.extract(pkCard, RANK_SIZE + COLOR_SIZE,
                Integer.SIZE - (RANK_SIZE + COLOR_SIZE));
        return (rank <= MAX_RANK && rank >= ZERO)
                && (color >= ZERO && color <= MAX_COLOR) && (rest == ZERO);
    }

    /**
     * @param c
     *            Color of the card
     * @param r
     *            Rank of the card
     * @return integer representing a packed card
     */
    public static int pack(Card.Color c, Card.Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }

    /**
     * @param pkCard
     *            packed representation of a card
     * @return Color of the card
     */
    public static Card.Color color(int pkCard) {
        assert (isValid(pkCard));
        int color = Bits32.extract(pkCard, COLOR_START, COLOR_SIZE);
        return Color.values()[color];
    }

    /**
     * @param pkCard
     *            packed representation of a card
     * @return Rank of the card
     */
    public static Card.Rank rank(int pkCard) {
        assert (isValid(pkCard));
        int rank = Bits32.extract(pkCard, RANK_START, RANK_SIZE);
        return Rank.values()[rank];
    }

    /**
     * @param trump
     *            The color that is trump
     * @param pkCardL
     *            The card we wish to compare
     * @param pkCardR
     *            The card we wish to compare TO
     * @return true if pkCardL is better than pkCardR
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert (isValid(pkCardL) && isValid(pkCardR));
        int colorL = Bits32.extract(pkCardL, COLOR_START, COLOR_SIZE);
        int colorR = Bits32.extract(pkCardR, COLOR_START, COLOR_SIZE);
        int rankL = Bits32.extract(pkCardL, RANK_START, RANK_SIZE);
        int rankR = Bits32.extract(pkCardR, RANK_START, RANK_SIZE);
        int trumpC = trump.ordinal();

        if (colorL == trumpC && colorR == trumpC)
            return rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal();
        else if (colorL == trumpC || colorR == trumpC)
            return colorL == trumpC;
        else if (colorR != colorL)
            return false; // Not comparable : not of the same kind and not trump
        else
            return rankL > rankR;
    }

    /**
     * @param trump
     *            The color that is currently trump
     * @param pkCard
     *            The card in packed representation
     * @return The points that card gives
     */
    public static int points(Card.Color trump, int pkCard) {
        assert (isValid(pkCard));

        Rank rank = rank(pkCard);
        Color color = color(pkCard);

        if (trump == color) { // Card has trump color
            if (rank == Rank.NINE)
                return 14;
            else if (rank == Rank.JACK)
                return 20;
        }
        switch (rank) {
        case TEN:
            return 10;
        case JACK:
            return 2;
        case QUEEN:
            return 3;
        case KING:
            return 4;
        case ACE:
            return 11;

        default:
            return 0; // For all the other cards
        }
    }

    /**
     * @param pkCard
     *            The card in packed form
     * @return The appearance of the card
     */
    public static String toString(int pkCard) {
        assert (isValid(pkCard));
        return color(pkCard).toString() + rank(pkCard).toString();
    }

}
