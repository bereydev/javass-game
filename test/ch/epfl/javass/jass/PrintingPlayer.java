package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
      this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      System.out.print("C'est à moi de jouer... Je joue : ");
      Card c = underlyingPlayer.cardToPlay(state, hand);
      System.out.println(c);
      return c;
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

    // … autres méthodes de Player (à écrire)
}
