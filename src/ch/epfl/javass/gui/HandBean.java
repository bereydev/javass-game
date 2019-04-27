/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 27, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;

public final class HandBean {
    
    private final static int HAND_SIZE = 9; 
    private List<Card> hand = Arrays.asList(new Card[HAND_SIZE]); 
    private List<Card> playableCards = Arrays.asList(new Card[HAND_SIZE]); 
    
    
    public void setHand(CardSet newHand) {
        //TODO : CHECK :D
        if(newHand.size()== 9)
            for(int i=0; i<9; i++)
                hand.add(newHand.get(i)); 
        else
            for(int i=0; i<9; i++) {
                if(i>newHand.size())
                    hand.add(null); 
                else
                    hand.add(newHand.get(i)); 
            }        
    }
}
