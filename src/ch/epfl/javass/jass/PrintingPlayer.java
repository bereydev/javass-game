package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    private static final int FIRST = 0;

    /**
     * @param underlyingPlayer
     *            The player to print
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
        underlyingPlayer.setPlayers(ownId, playerNames);
        System.out.println("Les joueurs sont : ");
        // TODO comment ittérer sur une map ?
        for (PlayerId p : playerNames.keySet()) {
            System.out.print(p);
            if (p.ordinal() == ownId.ordinal())
                System.out.println("(that's me)");
            else
                System.out.println("");
        }

    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
        System.out.print("Ma nouvelle main : ");
        System.out.println(newHand);
    }

    @Override
    public void setTrump(Color trump) {
        underlyingPlayer.setTrump(trump);
        System.out.print("Atout : ");
        System.out.println(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
        System.out.print("Pli " + newTrick.index() + ", commencé par "
                + newTrick.player(FIRST) + " :");
        System.out.println(newTrick);

    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
        System.out.print("Scores : ");
        System.out.println(score);

    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
        System.out.print("Et le vainqueur est ... l'équipe : ");
        System.out.println(winningTeam.name());
        System.out.println("Bravo !");

    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#trumpToPlay(ch.epfl.javass.jass.Card.Color, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color trumpToPlay(CardSet hand) {
        // TODO Auto-generated method stub
        return underlyingPlayer.trumpToPlay(hand);
    }

}
