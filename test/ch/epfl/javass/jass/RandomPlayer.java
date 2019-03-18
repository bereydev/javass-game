package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;

public final class RandomPlayer implements Player {
    private final Random rng;
    private CardSet hand;
    private Score score;
    private Color trump;
    private Trick trick;
    private TeamId winningTeam;
    //surement quelque chose de plus intelligent à faire mais la je vois pas ...
    

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
        // TODO ...
        
    }

    @Override
    public void updateHand(CardSet newHand) {
        this.hand = newHand;
        
    }

    @Override
    public void setTrump(Color trump) {
        this.trump = trump;
        
    }

    @Override
    public void updateTrick(Trick newTrick) {
        this.trick = newTrick;
        
    }

    @Override
    public void updateScore(Score score) {
        this.score = score;
        
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeam = winningTeam;
        
    }
}
