package ch.epfl.javass.jass;

import java.util.Random;

import ch.epfl.javass.jass.Card.Color;

public final class RandomPlayer implements Player {
    private final Random rng;

    /**
     * @param rngSeed
     *            The seed used for random generations of numbers
     */
    public RandomPlayer(long rngSeed) {
        this.rng = new Random(rngSeed);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        CardSet playable = state.trick().playableCards(hand);
        return playable.get(rng.nextInt(playable.size()));
    }

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#trumpToPlay(ch.epfl.javass.jass.Card.Color, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color trumpToPlay(CardSet hand) {
        // TODO Auto-generated method stub
        return Color.values()[rng.nextInt(Color.COUNT)];
    }

}
