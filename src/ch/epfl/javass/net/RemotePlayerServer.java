/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.net;

import ch.epfl.javass.jass.Player;

public final class RemotePlayerServer {
    
    private Player player; 
    
    public RemotePlayerServer(Player player) {
        this.player = player; 
    }
    
    public void run() {
        //TODO waits for the clients message
        //calls the correspondent function from the local player,
        //If cardToPlay, sends the value to the client 
    }

}
