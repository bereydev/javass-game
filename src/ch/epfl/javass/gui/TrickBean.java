/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 27, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.collections.ObservableMap;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.collections.FXCollections; 

public final class TrickBean {
    
    private ObjectProperty<Color> trump; 
    private ObservableMap<PlayerId,Card> trick; 
    private ObjectProperty<PlayerId> winningPlayer; 
    
    
    public void setTrump(Color trump) {
        this.trump.setValue(trump); 
    }
    
    public void setWinningPlayer(PlayerId player) {
        winningPlayer.setValue(player);
    }
    
    public void setTrick(Trick trick) {
        if(!trick.isEmpty())
            winningPlayer.setValue(trick.winningPlayer()); 
        
        for(int i=0; i<PlayerId.COUNT; i++) 
            this.trick.put(trick.player(i), trick.card(i)); 
        
    }
    
    public ObservableMap<PlayerId, Card> trick(){
       return FXCollections.unmodifiableObservableMap(trick); 
    }
    
    public ReadOnlyObjectProperty<Color> ColorProperty(){
        return trump; 
    }
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty(){
        return winningPlayer; 
    }

}
