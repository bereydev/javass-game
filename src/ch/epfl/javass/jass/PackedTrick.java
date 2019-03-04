/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 4, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;

public final class PackedTrick {

    private PackedTrick() {
        // not-instantiable class.
    }

    public static final int INVALID = ~0;

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick is correctly packed.
     */
    public static boolean isValid(int pkTrick) {
        for (int i = 0; i < 4; i++) {
            boolean aCardIsInvalid = false;
            int card = Bits32.extract(pkTrick, 4 * i, 4);

            // INVALID CARD CASE
            if (!PackedCard.isValid(card)) {
                if (card == PackedCard.INVALID)
                    aCardIsInvalid = true;
                else
                    return false;
            }
            // VALID CARD CASE
            else if (aCardIsInvalid) // Checks if there is an invalid card
                                     // before a valid one.
                return false;
        }
        int index = Bits32.extract(pkTrick, 24, 3);

        return index <= 8 && index >= 0;
    }
    
    /**
     * @param trump
     * @param firstPlayer
     * @return
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        
        return 0; 
    }
    
    public static int nextEmpty(int pkTrick) {
        return 0; 
    }
    public static boolean isLast(int pkTrick) {
        return true; 
    }
    public static boolean isEmpty(int pkTrick) {
        return true; 
    }
    public static boolean isFull(int pkTrick) {
        return true; 
    }
    public static int size(int pkTrick) {
        return 0; 
    }
    public static Color trump(int pkTrick) {
        return null; 
    }
    public static PlayerId player(int pkTrick, int index) {
        return null; 
    }
    public static int index(int pkTrick) {
        return 0; 
    }
    public static int card(int pkTrick, int index) {
        return 0; 
    }
    public static int withAddedCard(int pkTrick, int pkCard) {
        return 0; 
    }
    public static Color baseColor(int pkTrick) {
        return null; 
    }
    public static long playableCards(int pkTrick, long pkHand) {
        return 0; 
    }
    public static int points(int pkTrick) {
        return 0; 
    }
    public static PlayerId winningPlayer(int pkTrick) {
        return null; 
    }
    public static String toString(int pkTrick) {
        return null; 
    }
    
    
    

}
