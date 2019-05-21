/*
 *	Author : Alexandre Santangelo & Jonathan Bereyziat
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

/**
 * A javaFx Bean that contains the observable Properties of a Trick
 *
 */
public final class TrickBean {

    private ObjectProperty<Color> trump;
    private ObservableMap<PlayerId, Card> trick;
    private ObjectProperty<PlayerId> winningPlayer;

    public TrickBean() {
        trump = new SimpleObjectProperty<>();
        winningPlayer = new SimpleObjectProperty<>();
        trick = FXCollections.observableHashMap();
    }

    /**
     * Sets the new trump color for the bean
     * 
     * @param trump
     *            - new Color value
     */
    public void setTrump(Color trump) {
        this.trump.setValue(trump);
    }

    /**
     * Sets the new winning player of the trick for the bean
     * 
     * @param player
     *            - new PlayerId value
     */
    public void setWinningPlayer(PlayerId player) {
        winningPlayer.setValue(player);
    }

    /**
     * Sets the new value for the trick for the bean
     * 
     * @param trick
     *            - new Trick value
     */
    public void setTrick(Trick trick) {
        if (!trick.isEmpty())
            winningPlayer.setValue(trick.winningPlayer());
        else 
            winningPlayer.setValue(null);
        this.trick.clear(); 
        for (int i = 0; i < trick.size(); i++)
            this.trick.putIfAbsent(trick.player(i), trick.card(i));

    }

    /**
     * Allows to obtain the current trick
     * 
     * @return the trick value as an ObservableMar<PlayerID, Card>
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trick);
    }

    /**
     * Allows to obtain the trump of the trick
     * 
     * @return the trump Color as a ReadOnlyObjectProperty<Color>
     */
    public ReadOnlyObjectProperty<Color> ColorProperty() {
        return trump;
    }

    /**
     * Allows to obtain the winning player of the trick
     * 
     * @return the winning Player PlayerID as a ReadOnlyObjectProperty<PlayerId>
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayer;
    }

}
