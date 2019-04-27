/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 27, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.collections.ObservableMap;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections; 

public final class TrickBean {
    
    private Color trump; 
    private Map<PlayerId,Card> trick; 
    private PlayerId winningPlayer; 
    
    
    public void setTrump(Color trump) {
        this.trump = trump; 
    }
    
    public void setWinningPlayer(PlayerId player) {
        winningPlayer = player; 
    }
    
    public void setTrick(Trick trick) {
        if(!trick.isEmpty())
            winningPlayer = trick.winningPlayer(); 
        
        for(int i=0; i<PlayerId.COUNT; i++) 
            this.trick.put(trick.player(i), trick.card(i)); 
        
    }
    
    public ObservableMap<PlayerId, Card> trick(){
       return FXCollections.unmodifiableObservableMap(FXCollections.observableMap(trick)); 
    }
    
    public ReadOnlyObjectProperty<Color> ColorProperty(){
        return new SimpleObjectProperty<Color>(trump); 
    }
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty(){
        return new SimpleObjectProperty<PlayerId>(winningPlayer); 
    }

}
