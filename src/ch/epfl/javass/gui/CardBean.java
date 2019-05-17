/*
 *	Author : Alexandre Santangelo 
 *	Date   : May 14, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class CardBean {

    private ObjectProperty<Card> card = new SimpleObjectProperty<>();
    
    public void setCard(Card c) {
        card.setValue(c);
    }
    
    public ObjectProperty<Card> card(){
        return card; 
    }
}
