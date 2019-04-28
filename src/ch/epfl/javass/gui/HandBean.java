/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 27, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

public final class HandBean {

    private final static int HAND_SIZE = 9;
    private List<Card> hand = new ArrayList<>();
    private Set<Card> playableCards = new HashSet<>();
    
    public HandBean() {
        for(int i=0; i<9; i++) {
            hand.add(null); 
            playableCards.add(null); 
        }
    }

    public void setHand(CardSet newHand) {
        // TODO : CHECK :D
        for (int i = 0; i < HAND_SIZE; i++) {
            if (i > newHand.size())
                hand.add(null);
            else
                hand.add(newHand.get(i));
        }
        
    }

    public void setPlayableCards(CardSet newPlayableCards) {
        for (int i = 0; i < HAND_SIZE; i++) {
            if (i > newPlayableCards.size())
                playableCards.add(null);
            else
                playableCards.add(newPlayableCards.get(i));
        }
    }

    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(
                FXCollections.observableArrayList(hand));
    }

    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(
                FXCollections.observableSet(playableCards));
    }
}
