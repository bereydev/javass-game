/*
 *	Author : Alexandre Santangelo 

 *	Date   : Apr 27, 2019	
*/

package ch.epfl.javass.gui;

import java.util.Collections;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * javaFx Bean that contains the observable Property of the Hand
 *
 */
public final class HandBean {

    private final ObservableList<Card> hand;
    private final ObservableSet<Card> playableCards;
    
    
    public HandBean() {
        hand = FXCollections.observableArrayList(Collections.nCopies(Jass.HAND_SIZE, null));
        playableCards = FXCollections.observableSet();
    }
    
    /**
     * Sets the new value of the hand for the bean
     * 
     * @param newHand
     *            - the new CardSet of the hand
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE)
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                hand.set(i, newHand.get(i));
            }
        else
            for (int i = 0; i < Jass.HAND_SIZE; i++) {
                if (hand.get(i) != null && !newHand.contains(hand.get(i)))
                    hand.set(i, null);
            }

    }

    /**
     * Sets the new playable cards for the bean
     * 
     * @param newPlayableCards
     *            - the new CardSet of playable cards
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCards.clear();
        for (int i = 0; i < newPlayableCards.size(); i++) {
            playableCards.add(newPlayableCards.get(i));
        }
    }

    /**
     * Allows to get the hand
     * 
     * @return the hand as an ObservableList<Card>
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand);
    }

    /**
     * Allows to get the playable cards
     * 
     * @return the playable cards as an ObservableSet<Card>
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);

    }
}
