/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;


/**
 * @author Jonathan Bereyziat
 *
 */
public class CardSet {

    /**
     * private constructor you can't instantiate
     */
    private CardSet() {
    }
    
    public static final List<Card> EMPTY = new ArrayList<Card>();
    public static final List<Card> ALL_CARDS = new ArrayList<Card>();
    
    public static CardSet of(List<Card> cards) {
        return new CardSet();
    }
}
