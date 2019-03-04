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
    public static int packed() {
        return 0; 
    }
    public static Trick nextEmpty() {
        return null; 
    }
    public static boolean isEmpty() {
        return false; 
    }
    public static boolean isFull() {
        return false; 
    }
    public static boolean isLast() {
        return false; 
    }
    public static int size() {
        return 0; 
    }
    public static Color trump() {
        return null; 
    }
    public static int index() {
        return 0; 
    }
    public static PlayerId player(int index) {
        return null; 
    }
    public static Card card(int index) {
        return null; 
    }
    public static Trick withAddedCard(Card c) {
        return null; 
    }
    public static Color baseColor() {
        return null; 
    }
    public static CardSet playableCards(CardSet hand) {
        return null; 
    }
    public static int points() {
        return 0; 
    }
    public static PlayerId winningPlayer() {
        return null; 
    }
}

