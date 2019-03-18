/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 13, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

import java.util.Collections;
import java.util.EnumMap;
import java.util.LinkedList;
import java.util.List;

public final class JassGame {

    private long rngSeed;
    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> hands;
    private Random shuffleRng;
    private Random trumpRng;
    private List<Card> cards = new LinkedList();
    private TurnState turnState;
    private static final int CARDS_PER_HAND = 9;

    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));
        // Initialization of the list of cards.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                cards.add(Card.ofPacked(j + (i << 4)));
            }
        }
        deal();
        turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                Score.INITIAL, firstPlayer());

    }

    /**
     * @return True if the game is over (a team has 1000points)
     */
    public boolean isGameOver() {
        return PackedScore.totalPoints(turnState.packedScore(),
                TeamId.TEAM_1) == 1000
                || PackedScore.totalPoints(turnState.packedScore(),
                        TeamId.TEAM_2) == 1000;
    }

    /**
     * Advances to the end of the next trick, doing everything.
     */
    public void advanceToEndOfNextTrick() {

    }

    private void deal() {
        Collections.shuffle(cards, shuffleRng);
        for (PlayerId p : hands.keySet()) {
            CardSet hand = CardSet.EMPTY; 
            for (int i = CARDS_PER_HAND * p.ordinal(); i < CARDS_PER_HAND
                    * (p.ordinal() + 1); i++) {
                hand.add(cards.get(i)); 
                // System.out.println("added card "+
                // PackedCard.toString(cards.get(i)));
            }
            hands.put(p, hand);
        }

    }

    private PlayerId firstPlayer() {
        for (PlayerId p : hands.keySet()) {
            if (hands.get(p).contains(Card.of(Color.DIAMOND, Rank.SEVEN)))
                return p;

        }
        return null;
    }

    private void play() {
        for (PlayerId p : players.keySet()) {
            players.get(p).updateHand(hands.get(p));
            players.get(p).updateTrick(turnState.trick().withAddedCard(players.get(p).cardToPlay(turnState, hands.get(p))));
        }

    }

    public static void main(String[] args) {
        // JassGame test = new JassGame(2019,,);
    }
}
