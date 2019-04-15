/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.net;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.TurnState;

/**
 * @author astra
 *
 */
public final class RemotePlayerClient implements Player, AutoCloseable {
    
    public RemotePlayerClient(String name) {
        
    }
    
    
    @Override
    public void close() throws Exception {
        // TODO Close every stream 

    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        // TODO Auto-generated method stub
        return null;
    }

}
