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
    private static final int CARD_SIZE = 6; 
    private static final int  TRICKS_BY_TURN = 8; 

    /**
     * @param pkTrick
     *            A packed trick.
     * @return True if the trick is correctly packed.
     */
    public static boolean isValid(int pkTrick) {
        for (int i = 0; i < CARD_SIZE; i++) {
            boolean aCardIsInvalid = false;
            int card = Bits32.extract(pkTrick, CARD_SIZE * i, CARD_SIZE);

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
     * @param trump        The trump color of the trick 
     * @param firstPlayer   The given player
     * @return
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
         
        return Bits32.pack(PackedCard.INVALID,CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE, 0, 3, firstPlayer.ordinal(), 2, trump.ordinal(), 2);
    }
    
    /**
     * @param pkTrick   A packed trick.
     * @return  The next trick, with no cards 
     */
    public static int nextEmpty(int pkTrick) {
        if(isLast(pkTrick))
            return INVALID; 
        return Bits32.pack(PackedCard.INVALID,CARD_SIZE, PackedCard.INVALID, CARD_SIZE, PackedCard.INVALID, CARD_SIZE,
                PackedCard.INVALID, CARD_SIZE, Bits32.extract(pkTrick, 24, 3)+1, 3, Bits32.extract(pkTrick, 28, 2), 2, Bits32.extract(pkTrick, 30, 2), 2); 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  True if this is the last trick of the turn 
     */
    public static boolean isLast(int pkTrick) {
        assert(isValid(pkTrick)); 
        return Bits32.extract(pkTrick, 24, 3) == TRICKS_BY_TURN; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  True if the trick has no cards. 
     */
    public static boolean isEmpty(int pkTrick) {
        assert(isValid(pkTrick)); 
        for(int i=0; i<4; i++) {
           if(Bits32.extract(pkTrick, CARD_SIZE*i, CARD_SIZE)!= INVALID)
               return false; 
        }
        return true; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  True if the trick has all 4 cards. 
     */
    public static boolean isFull(int pkTrick) {
        assert(isValid(pkTrick)); 
        for(int i=0; i<4; i++) {
            if(Bits32.extract(pkTrick, CARD_SIZE*i, CARD_SIZE)== INVALID)
                return false; 
         }
         return true; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The number of cards in the trick. 
     */
    public static int size(int pkTrick) {
        assert(isValid(pkTrick));
        for(int i=0; i<4; i++) {
            if(Bits32.extract(pkTrick, CARD_SIZE*i, CARD_SIZE)== INVALID)
                return i+1;  
         }
        return 4; // All cards. 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The color of the trump 
     */
    public static Color trump(int pkTrick) {
        assert(isValid(pkTrick));
        return Card.Color.values()[Bits32.extract(pkTrick,28, 2)]; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @param index
     * @return
     */
    public static PlayerId player(int pkTrick, int index) {
        //TODO : Je comprend pas la fonction 
        return null; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The index of the trick 
     */
    public static int index(int pkTrick) {
        assert(isValid(pkTrick));
        return Bits32.extract(pkTrick, 24, 3); 
    }
    /**
     * @param pkTrick   A packed trick.
     * @param index     The index of the card (0,1,2 or 3) 
     * @return  The pkCard at that index 
     */
    public static int card(int pkTrick, int index) {
        assert(isValid(pkTrick));
        //HERE I SUPPOSE THAT THE INDEX IS BETWEEN 0 AND 3 
        return Bits32.extract(pkTrick, index*CARD_SIZE, CARD_SIZE); 
    }
    /**
     * @param pkTrick   A packed trick.
     * @param pkCard    A packed card. 
     * @return  pkTrick with an added card, pkCard. 
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        for(int i=0; i<4; i++) {
            if(Bits32.extract(pkTrick, CARD_SIZE*i, CARD_SIZE)== INVALID)
                return pkTrick | pkCard << CARD_SIZE*i ; 
         }
        return pkTrick; //In case the Trick is full. 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The color of the card that was first played 
     */
    public static Color baseColor(int pkTrick) {
        return Card.Color.values()[Bits32.extract(pkTrick,4, 2)];
    }
    /**
     * @param pkTrick   A packed trick.
     * @param pkHand    A packed hand of cards 
     * @return  A collection of all the playable cards 
     */
    public static long playableCards(int pkTrick, long pkHand) {
        return 0; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The points of the trick 
     */
    public static int points(int pkTrick) {
        int total =0; 
        Color trump = Color.values()[Bits32.extract(pkTrick, 30, 2)]; 
        for(int i=0; i<4; i++) {
          total+= PackedCard.points(trump, Bits32.extract(pkTrick, i*CARD_SIZE, CARD_SIZE)); 
        }
        if(isLast(pkTrick))  total+=5; 
        
        return total; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The winning player of the trick 
     */
    public static PlayerId winningPlayer(int pkTrick) {
        return null; 
    }
    /**
     * @param pkTrick   A packed trick.
     * @return  The string version of the trick 
     */
    public static String toString(int pkTrick) {
        return null; 
    }
    
    
    

}
