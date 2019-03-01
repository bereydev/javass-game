package ch.epfl.javass.jass;

import java.util.List;
import java.util.Collections;
import java.util.Arrays;

/**
 * @author : Alexandre Santangelo Date : Feb 19, 2019
 */
public final class Card {

    private Color color;
    private Rank rank;

    private Card(Color c, Rank r) {
        rank = r;
        color = c;
    }

    /**
     * @param c
     *            The color of the card
     * @param r
     *            The rank of the card
     * @return The card
     */
    public static Card of(Color c, Rank r) {
        return new Card(c, r);
    }

    /**
     * @param packed
     *            Packed Card you want to create
     * @return A card
     */
    public static Card ofPacked(int packed) {
        Color c = PackedCard.color(packed);
        Rank r = PackedCard.rank(packed);

        return new Card(c, r);
    }

    /**
     * @return The packed version of the card
     */
    public int packed() {
        return PackedCard.pack(this.color, this.rank);
    }

    /**
     * @return The color of the card
     */
    public Color color() {
        return this.color;
    }

    /**
     * @return The rank of the card
     */
    public Rank rank() {
       return this.rank;
    }

    /**
     * @param trump
     *            The trump color
     * @param that
     *            The card you wish to compare to
     * @return True if the card you applied the method to is better than the
     *         other one
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.packed(), that.packed());
    }

    /**
     * @param trump
     *            The trump color
     * @return The points assigned to that card
     */
    public int points(Color trump) {
        return PackedCard.points(trump, this.packed());
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#equals(java.lang.Object)
     */
    public boolean equals(Object thatO) {
        
        if (thatO instanceof Card) {
            if (((Card) thatO).packed() == this.packed())
                return true;
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        return this.packed();
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return PackedCard.toString(this.packed());
    }

    public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");

        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 4;
        private String symbol;

        private Color(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }
    }

    public enum Rank {
        SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK(
                "J"), QUEEN("Q"), KING("K"), ACE("A");

        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 9;

        private String symbol;

        /**
         * @return The ordinal of a card whose color is the same as the trump
         */
        public int trumpOrdinal() {
            int num = this.ordinal();
            int trumpOrdinal;
            switch (num) {
            case 3:
                trumpOrdinal = 7;
                break;
            case 4:
                trumpOrdinal = 3;
                break;
            case 5:
                trumpOrdinal = 8;
                break;
            case 6:
                trumpOrdinal = 4;
                break;
            case 7:
                trumpOrdinal = 5;
                break;
            case 8:
                trumpOrdinal = 6;
                break;
            // the 3 first cards don't change of value when trump
            default:
                trumpOrdinal = this.ordinal();
                break;
            }
            return trumpOrdinal;
        }

        private Rank(String symbol) {
            this.symbol = symbol;
        }

        public String toString() {
            return symbol;
        }
    }

}
