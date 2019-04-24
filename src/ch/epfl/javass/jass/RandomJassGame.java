package ch.epfl.javass.jass;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.HashMap;
import java.util.Map;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;

/**
 * @author Alexandre Santangelo, Jonathan Bereyziat
 * 
 *         Simulates a JassGame.
 *
 */
public final class RandomJassGame {
    public static void main(String[] args) {
        final int seed = 2019;

        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        
        for (PlayerId pId : PlayerId.ALL) {
            Player player = new RandomPlayer(seed);
            if (pId == PlayerId.PLAYER_2)
               try {
                   player = new PrintingPlayer(new RemotePlayerClient("localhost"));
               }
            catch(IOException e){
                throw new UncheckedIOException(e); 
            }
            players.put(pId, player);
            playerNames.put(pId, pId.name());
            
        }

        JassGame g = new JassGame(seed, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
        }
        
    }
}
