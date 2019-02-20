/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 19, 2019	
*/
package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public class PackedCard {
    
    public static int INVALID = 63; 
    
    public static boolean isValid(int pkCard) {
        // TODO 
        return true; 
    }
    
    public static int pack(Card.Color c, Card.Rank r) {
        //TODO
        return 0; 
    }
    public static Card.Color color(int pkCard) {
        //TODO
        return Color.CLUB; 
    }
    
    public static Card.Rank rank(int pkCard) {
        //TODO
        return Rank.JACK;  
    }
    
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        //TODO 
        return true; 
    }
    
    public static int points(Card.Color trump, int pkCard) {
        //TODO 
        return 0; 
    }
    public static String toString(int pkCard) {
        //TODO 
        return "Not implemented"; 
    }

}
