/*
 *	Author : Alexandre Santangelo 

 *	Date   : Apr 27, 2019	
*/

package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * javaFx Bean that contains the observable Property of the Hand
 *
 */
public final class HandBean {

    private ObservableList<Card> hand = FXCollections.observableArrayList();
    private ObservableSet<Card> playableCards = FXCollections.observableSet();

    public HandBean() {
        for (int i = 0; i < 9; i++) {
            hand.add(null);
        }
    }

    /**
     * Sets the new value of the hand for the bean
     * 
     * @param newHand
     *            - the new CardSet of the hand
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == 9)
            for (int i = 0; i < 9; i++) {
                hand.set(i, newHand.get(i));
            }
        else
            for (int i = 0; i < 9; i++) {
                if (hand.get(i) == null)
                    continue;
                if (!newHand.contains(hand.get(i)))
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
