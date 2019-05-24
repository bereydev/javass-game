/*
 *	Author : Alexandre Santangelo 
 *	Date   : May 20, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card.Color;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;


public final class TrumpBean {

    
    private ObjectProperty<Color> trump = new SimpleObjectProperty<>(); 
    
    public void setTrump(Color trump) {
        this.trump.setValue(trump);
    }
    
    public ObjectProperty<Color> trump(){
        return trump; 
    }
}
