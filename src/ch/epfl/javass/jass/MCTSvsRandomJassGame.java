package ch.epfl.javass.jass;

import java.awt.Toolkit;
import java.util.HashMap;
import java.util.Map;

public final class MCTSvsRandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();

        for (PlayerId pId: PlayerId.ALL) {
          Player player = new RandomPlayer(0);
          if (pId == PlayerId.PLAYER_2 || pId == PlayerId.PLAYER_4 )
              player = new MctsPlayer(pId,0,100);
          players.put(pId, player);
          playerNames.put(pId, pId.name());
        }
        int wins =0; 
        for (int i = 0; i < 100; i++) {
            JassGame g = new JassGame(i, players, playerNames);
            while (! g.isGameOver()) {
              g.advanceToEndOfNextTrick();
            }
            if(g.winner().equals(TeamId.TEAM_2)) {
                wins++; 
                System.out.println(i);
            }
                    
            
        }
        System.out.println(wins+ "WINSSSSSSSSSSS");
        
      }
}
