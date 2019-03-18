package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;

public final class RandomPlayer implements Player {
    private final Random rng;

    public RandomPlayer(long rngSeed) {
      this.rng = new Random(rngSeed);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      CardSet playable = state.trick().playableCards(hand);
      return playable.get(rng.nextInt(playable.size()));
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateHand(CardSet newHand) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setTrump(Color trump) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateTrick(Trick newTrick) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void updateScore(Score score) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        // TODO Auto-generated method stub
        
    }
}
