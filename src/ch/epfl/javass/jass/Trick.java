/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 4, 2019	
*/


package ch.epfl.javass.jass;

import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;

public final class Trick {

    private Trick(int pkTrick) {
        this.pkTrick = pkTrick;

    }
    
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    private int pkTrick;

    /**
     * @param trump
     * @param firstPlayer
     * @return Same behavior as PackedTrick's functions
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * @param packed
     *            A Packed Trick
     * @return The unpacked version of the trick
     */
    public static Trick ofPacked(int packed) {
        Preconditions.checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * @return The unpacked version of the trick
     */
    public int packed() {
        return pkTrick;
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public Trick nextEmpty() {
        if (!isFull()) {
            throw new IllegalStateException();
        }
        return new Trick(PackedTrick.nextEmpty(pkTrick));
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * @param index
     * @return Same behavior as PackedTrick's functions
     */
    public PlayerId player(int index) {
        if (!(index >=0 && index < 4)) {
            throw new IndexOutOfBoundsException();
        }
        return PackedTrick.player(pkTrick, index);
    }

    /**
     * @param index
     * @return Same behavior as PackedTrick's functions
     */
    public Card card(int index) {
        if (!(index >=0 && index < 4)) {
            throw new IndexOutOfBoundsException();
        }
        return Card.ofPacked(PackedTrick.card(pkTrick, index));
    }

    /**
     * @param c
     * @return Same behavior as PackedTrick's functions
     */
    public Trick withAddedCard(Card c) {
        return new Trick(PackedTrick.withAddedCard(pkTrick, c.packed()));
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public Color baseColor() {
        return PackedTrick.baseColor(pkTrick);
    }

    /**
     * @param hand
     * @return Same behavior as PackedTrick's functions
     */
    public CardSet playableCards(CardSet hand) {

        return CardSet
                .ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * @return Same behavior as PackedTrick's functions
     */
    public PlayerId winningPlayer() {
        if (isEmpty()) {
            throw new IllegalStateException();
        }
        return PackedTrick.winningPlayer(pkTrick);
    }
    
    @Override
    public boolean equals(Object other) {
        if(other instanceof Trick) {
            if(((Trick) other).pkTrick == pkTrick)
                return true; 
        }
        return false; 
    }
    
    @Override
    public int hashCode() {
        return pkTrick; 
    }
    
    @Override
    public String toString() {
        return PackedTrick.toString(pkTrick);
    }
}
