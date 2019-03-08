/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 4, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;

public final class Trick {

    public final static Trick INVALID = new Trick(); 
    
    public Trick() {
        //TODO (remember : class is immutable) 
    }
    
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return null; 
    }
    public static Trick ofPacked(int packed) {
        return null; 
    }
    public int packed() {
        return 0; 
    }
    public Trick nextEmpty() {
        return null; 
    }
    public boolean isEmpty() {
        return false; 
    }
    public boolean isFull() {
        return false; 
    }
    public boolean isLast() {
        return false; 
    }
    public int size() {
        return 0; 
    }
    public Color trump() {
        return null; 
    }
    public int index() {
        return 0; 
    }
    public PlayerId player(int index) {
        return null; 
    }
    public Card card(int index) {
        return null; 
    }
    public Trick withAddedCard(Card c) {
        return null; 
    }
    public Color baseColor() {
        return null; 
    }
    public CardSet playableCards(CardSet hand) {
        return null; 
    }
    public int points() {
        return 0; 
    }
    public PlayerId winningPlayer() {
        return null; 
    }
}

