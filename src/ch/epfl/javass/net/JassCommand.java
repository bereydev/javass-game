/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/
package ch.epfl.javass.net;

public enum JassCommand {
    
    PLRS("setPlayers"), 
    TRMP("setTrump"),
    HAND("updateHand"),
    TRCK("updateTrick"),
    CARD("cardToPlay"),
    SCOR("updateScore"),
    WINR("setWinningTeam"); 
    
    private String commandValue;
    
    JassCommand(String s){
        commandValue = s;
    }
    
    @Override
    public String toString() {
        return commandValue;
    }
}
