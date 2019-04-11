package ch.epfl.javass.jass;

import java.util.List;

import java.util.Collections;
import java.util.Arrays;

/**
 * @author : Alexandre Santangelo Date : Feb 19, 2019
 */
public final class Card {

    private int pkCard; 

    private Card(Color c, Rank r) {
        pkCard = PackedCard.pack(c, r);  
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
        return pkCard; 
    }

    /**
     * @return The color of the card
     */
    public Color color() {
        return PackedCard.color(pkCard);
    }

    /**
     * @return The rank of the card
     */
    public Rank rank() {
       return PackedCard.rank(pkCard); 
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

    @Override 
    public boolean equals(Object thatO) {
        
        if (thatO instanceof Card) {
            if (((Card) thatO).packed() == this.packed())
                return true;
        }
        return false;
    }

    @Override 
    public int hashCode() {
        return this.packed();
    }

    @Override
    public String toString() {
        return PackedCard.toString(this.packed());
    }

    public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");

        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        public static final int COUNT = 4;
        private String symbol;

        /**
         * @param symbol    The symbol to print. 
         */
        private Color(String symbol) {
            this.symbol = symbol;
        }
        
        @Override 
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
            switch (num) {
            case 3:
                return 7;
            case 4:
                return 3;
            case 5:
                return 8;
            case 6:
                return 4;
            case 7:
                return 5;
            case 8:
                return 6;
            // the 3 first cards don't change of value when trump
            default:
                return num;
            }
        }

        /**
         * @param symbol    The symbol to print. 
         */
        private Rank(String symbol) {
            this.symbol = symbol;
        }
        
        @Override
        public String toString() {
            return symbol;
        }
    }

}
