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
     * @return The same trick but without any card. 
     */
    public Trick nextEmpty() {
        if (!isFull()) {
            throw new IllegalStateException();
        }
        return new Trick(PackedTrick.nextEmpty(pkTrick));
    }

    /**
     * @return True if there is no card in the trick, false if there are 
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(pkTrick);
    }

    /**
     * @return True if the trick has 4 cards, false if not. 
     */
    public boolean isFull() {
        return PackedTrick.isFull(pkTrick);
    }

    /**
     * @return true if it is the last trick of the game, false if not 
     */
    public boolean isLast() {
        return PackedTrick.isLast(pkTrick);
    }

    /**
     * @return the number of cards in the trick 
     */
    public int size() {
        return PackedTrick.size(pkTrick);
    }

    /**
     * @return The trump of the trick 
     */
    public Color trump() {
        return PackedTrick.trump(pkTrick);
    }

    /**
     * @return The index of the trick 
     */
    public int index() {
        return PackedTrick.index(pkTrick);
    }

    /**
     * @param index
     * @return The player at index, index in the trick 
     */
    public PlayerId player(int index) {
        if (!(index >=0 && index < 4)) {
            throw new IndexOutOfBoundsException();
        }
        return PackedTrick.player(pkTrick, index);
    }

    /**
     * @param index An index between 0 and 3. 
     * @return The card at index index in the trick 
     */
    public Card card(int index) {
        if (!(index >=0 && index < 4)) {
            throw new IndexOutOfBoundsException();
        }
        return Card.ofPacked(PackedTrick.card(pkTrick, index));
    }

    /**
     * @param c The card to add
     * @return The trick with an added card. 
     */
    public Trick withAddedCard(Card c) {
        return new Trick(PackedTrick.withAddedCard(pkTrick, c.packed()));
    }

    /**
     * @return The base color of the trick 
     */
    public Color baseColor() {
        return PackedTrick.baseColor(pkTrick);
    }

    /**
     * @param hand  Your hand of cards 
     * @return The set of playable cards given your hand and Jass rules 
     */
    public CardSet playableCards(CardSet hand) {

        return CardSet
                .ofPacked(PackedTrick.playableCards(pkTrick, hand.packed()));
    }

    /**
     * @return The points of the trick 
     */
    public int points() {
        return PackedTrick.points(pkTrick);
    }

    /**
     * @return The currently winning player. 
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
