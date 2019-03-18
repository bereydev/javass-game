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
        System.out.print("Les joueurs sont : ");
        underlyingPlayer.setPlayers(ownId, playerNames);
        //TODO comment ittérer sur une map ?
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.print("Ma nouvelle main : ");
        underlyingPlayer.updateHand(newHand);
        System.out.println(newHand);
    }

    @Override
    public void setTrump(Color trump) {
        System.out.print("Atout : ");
        underlyingPlayer.setTrump(trump);
        System.out.println(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.print("Pli " + newTrick.index() + ", commencé par " + newTrick.player(0) + " :");
        underlyingPlayer.updateTrick(newTrick);
        System.out.println(newTrick);
        
    }

    @Override
    public void updateScore(Score score) {
        System.out.print("Scores : ");
        underlyingPlayer.updateScore(score);
        System.out.println(score);
        
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.print("Et le vainqueur est ... l'équipe : ");
        underlyingPlayer.setWinningTeam(winningTeam);
        System.out.println(winningTeam.name());
        System.out.println("Bravo !");
        
    }

}
