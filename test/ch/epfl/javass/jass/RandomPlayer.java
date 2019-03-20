package ch.epfl.javass.jass;

import java.util.Random;

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
    
//    //TEST
//    @Override
//    public void updateTrick(Trick newTrick) {
//        System.out.print("Pli " + newTrick.index() + ", commencé par " + newTrick.player(0) + " :");
//        System.out.println(newTrick);
//        
//    }
}
