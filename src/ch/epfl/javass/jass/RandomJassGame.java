package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

/**
 * @author Alexandre Santangelo, Jonathan Bereyziat
 * 
 * Simulates a JassGame. 
 *
 */
public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId: PlayerId.ALL) {
          Player player = new RandomPlayer(2019);
          if (pId == PlayerId.PLAYER_2)
              player = new PrintingPlayer(new RandomPlayer(2019));
          players.put(pId, player);
          playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(2019, players, playerNames);
        while (! g.isGameOver()) {
          g.advanceToEndOfNextTrick();
        }
      }
}
