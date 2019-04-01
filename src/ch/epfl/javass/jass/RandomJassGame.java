package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.Map;

public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId: PlayerId.ALL) {
          Player player = new RandomPlayer(0);
          if (pId == PlayerId.PLAYER_2)
              player = new PrintingPlayer(new MctsPlayer(PlayerId.PLAYER_2,0,100000));
          players.put(pId, player);
          playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(0, players, playerNames);
        while (! g.isGameOver()) {
          g.advanceToEndOfNextTrick();
        }
      }
}
