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

public final class HandBean {

    private ObservableList<Card> hand = FXCollections.observableArrayList(); 
    private ObservableSet<Card> playableCards = FXCollections.observableSet(); 
    
    public HandBean() {
        for(int i=0; i<9; i++) {
            hand.add(null); 
            playableCards.add(null); 
        }
    }

    public void setHand(CardSet newHand) {
        if(newHand.size()==9)
            for(int i=0; i<9; i++) {
                hand.set(i, newHand.get(i)); 
            }
        else
            for(int i=0; i<9; i++) {
                if(hand.get(i) == null)
                    continue; 
                if(!newHand.contains(hand.get(i)))
                    hand.set(i, null); 
            }
        
    }

    public void setPlayableCards(CardSet newPlayableCards) {
        if(newPlayableCards.size()==9) {
            playableCards.clear();
            for(int i=0; i<9; i++) {
                playableCards.add(newPlayableCards.get(i)); 
            }
        }
        else
            for(Card c : playableCards) {
                if(c == null)
                    continue; 
                if(!newPlayableCards.contains(c))
                    playableCards.remove(c); 
                    playableCards.add(null); 
            }
    }

    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(hand); 
    }

    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCards);
    }
}
