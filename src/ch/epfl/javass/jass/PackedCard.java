/*
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 19, 2019	
*/
package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;


public class PackedCard {
    
    public static int INVALID = 63; 
    
    
    /**
     * @param pkCard    A card in packed representation 
     * @return true if the packed card corresponds to a card 
     */
    public static boolean isValid(int pkCard) {
        int rank = Bits32.extract(pkCard, 0, 4); 
        int color = Bits32.extract(pkCard, 4, 2); 
        int rest = Bits32.extract(pkCard, 6, Integer.SIZE-6); //Bits that should be 0
        return (rank <=8 && rank>=0)&&(color>=0 && color<=3)&&(rest==0); 
    }
    
    /**
     * @param c Color of the card
     * @param r Rank of the card 
     * @return  An int that represents the card 
     */
    public static int pack(Card.Color c, Card.Rank r) {
        
        return Bits32.pack(r.ordinal(), 4, c.ordinal(), 2); 
    }
    
    /**
     * @param pkCard    packed representation of a card
     * @return  Color of the card 
     */
    public static Card.Color color(int pkCard) {
        assert isValid(pkCard);
        int color = Bits32.extract(pkCard, 4, 2); 
        return Color.values()[color]; 
    }
    /**
     * @param pkCard    packed representation of a card
     * @return  Rank of the card 
     */
    public static Card.Rank rank(int pkCard) {
        assert isValid(pkCard);
        int rank = Bits32.extract(pkCard, 0, 4); 
        return Rank.values()[rank];  
    }
    
    /**
     * @param trump     The color that is trump 
     * @param pkCardL   The card we wish to compare
     * @param pkCardR   The card we wish to compare TO 
     * @return  true if pkCardL is better than pkCardR
     */
    public static boolean isBetter(Card.Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL)&& isValid(pkCardR);
        int LValue, RValue; 
        
        if(color(pkCardL)==trump || color(pkCardR) == trump) {
            if(color(pkCardL)==trump) LValue = rank(pkCardL).trumpOrdinal(); 
            else LValue = rank(pkCardL).ordinal(); 
            if(color(pkCardR)==trump) RValue = rank(pkCardR).trumpOrdinal(); 
            else RValue = rank(pkCardR).ordinal(); 
            
            return LValue > RValue; 
        }
        else if(Bits32.extract(pkCardR, 4, 2)!=Bits32.extract(pkCardL, 4, 2))
            return false; //Not comparable as they are not of the same kind and not trump 
        else 
            return Bits32.extract(pkCardL, 0, 4)>Bits32.extract(pkCardR, 0, 4); 
    }
    
    /**
     * @param trump     The color that is currently trump
     * @param pkCard    The card in packed representation
     * @return          The points that card gives 
     */
    public static int points(Card.Color trump, int pkCard) {
        int rank = Bits32.extract(pkCard, 0, 4); 
        
        if(trump.ordinal() == Bits32.extract(pkCard, 4, 2)) {    //Card has trump color 
           if(rank==3) return 14; 
           else if(rank ==5) return 20; 
        }
        switch(rank) {
            case 4 : return 10; 
            case 5 : return 2; 
            case 6 : return 3; 
            case 7 : return 4; 
            case 8 : return 11; 
            
            default : return 0;   // For all the other cards 
        }
    }
    /**
     * @param pkCard    The card in packed form
     * @return          The appearance of the card
     */
    public static String toString(int pkCard) {
        return color(pkCard).toString() + rank(pkCard).toString(); 
    }

}
