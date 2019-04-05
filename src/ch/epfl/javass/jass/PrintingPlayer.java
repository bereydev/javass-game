package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    /**
     * @param underlyingPlayer  The player to print 
     */
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
        System.out.println("Les joueurs sont : ");
        //TODO comment ittérer sur une map ?
        for(PlayerId p : playerNames.keySet()) {
            System.out.print(p);
            if(p.ordinal() == ownId.ordinal())
                System.out.println("(that's me)");
            else 
                System.out.println("");
        }
            
            
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.print("Ma nouvelle main : ");
        System.out.println(newHand);
    }

    @Override
    public void setTrump(Color trump) {
        System.out.print("Atout : ");
        System.out.println(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.print("Pli " + newTrick.index() + ", commencé par " + newTrick.player(0) + " :");
        System.out.println(newTrick);
        
    }

    @Override
    public void updateScore(Score score) {
        System.out.print("Scores : ");
        System.out.println(score);
        
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.print("Et le vainqueur est ... l'équipe : ");
        System.out.println(winningTeam.name());
        System.out.println("Bravo !");
        
    }

}
