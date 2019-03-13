/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 13, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Random;
import java.util.Collections;
import java.util.EnumMap;

public final class JassGame {
    
    private long rngSeed; 
    private Map<PlayerId,Player> players; 
    private Map<PlayerId,String> playerNames; 
    private Random shuffleRng; 
    private Random trumpRng; 

   public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
       
       Random rng = new Random(rngSeed);
       this.shuffleRng = new Random(rng.nextLong());
       this.trumpRng = new Random(rng.nextLong());
       
       this.players = Collections.unmodifiableMap(new EnumMap<>(players)); 
       this.playerNames = Collections.unmodifiableMap(new EnumMap<>(playerNames)); 
       
       
       
    }
   
   
       /**
     * @return
     */
    boolean isGameOver() {
           return true; 
       }
       
    
    /**
     * 
     */
    void advanceToEndOfNextTrick() {
           
       }
}
