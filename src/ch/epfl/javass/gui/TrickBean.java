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
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections; 

public final class TrickBean {
    
    private ObjectProperty<Color> trump; 
    private ObservableMap<PlayerId,Card> trick; 
    private ObjectProperty<PlayerId> winningPlayer; 
    
    TrickBean(){
        trump = new SimpleObjectProperty<>(); 
        winningPlayer = new SimpleObjectProperty<>();  
        trick = FXCollections.observableHashMap(); 
    }
    
    
    public void setTrump(Color trump) {
        this.trump.setValue(trump); 
    }
    
    public void setWinningPlayer(PlayerId player) {
        winningPlayer.setValue(player);
    }
    
    public void setTrick(Trick trick) {
        if(trick.isEmpty()) {
            this.trick.clear();
            //TODO besoin de faire ça ou pas ?
            winningPlayer.setValue(null);
        }  
        else {
            winningPlayer.setValue(trick.winningPlayer()); 
            for(int i=0; i<trick.size(); i++) 
                this.trick.putIfAbsent(trick.player(i), trick.card(i)); 
        }
        
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
