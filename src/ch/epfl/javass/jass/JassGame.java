/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 13, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.Map;
import java.util.Random;

import org.hamcrest.core.IsNot;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

public final class JassGame {

    private Map<PlayerId, Player> players;
    private Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> hands = new HashMap<PlayerId, CardSet>();
    private List<PlayerId> playersInOrder = new LinkedList<PlayerId>();
    private Random shuffleRng;
    private boolean isNewGame = true;
    private PlayerId turnStarter;
    private Random trumpRng;

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
        // Initialization of turnState (to be modified later)
        turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                Score.INITIAL, PlayerId.PLAYER_1);
    }

    private void initializeGame() {

    }

    /**
     * @return True if the game is over (a team has 1000points)
     */

    public boolean isGameOver() {
        return turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                || turnState.score()
                        .totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS;
    }

    /**
     * Advances to the end of the next trick, doing everything.
     */

    public void advanceToEndOfNextTrick() {

        if (isGameOver())
            return;
        if (isNewGame) {
            isNewGame = false;
            deal();
            organizePlayers(firstPlayer());
            turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                    turnState.score().nextTurn(), firstPlayer());
        } else {
            if (turnState.isTerminal()) {
                isNewGame = false;
                deal();
                organizePlayers(turnState.nextPlayer());
                turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                        turnState.score().nextTurn(), turnState.nextPlayer());
            }
            deal();
            organizePlayers(turnStarter());
            turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                    turnState.score().nextTurn(), turnStarter());
        }
        for (PlayerId p : playersInOrder) {
             players.get(p).setPlayers(PlayerId.PLAYER_1, playerNames);
             players.get(p).updateHand(hands.get(PlayerId.PLAYER_1));
             players.get(p).setTrump(turnState.trick().trump());
             }

             for (PlayerId p : playersInOrder) {
             players.get(p).updateScore(turnState.score());
             }
            
             for (PlayerId p : playersInOrder) {
            
             for (PlayerId q : playersInOrder) {
             players.get(q).updateTrick(turnState.trick());
             }
            
             Card cardToPlay = players.get(p).cardToPlay(turnState,
             hands.get(p));
            
             players.get(p).updateHand(hands.get(p).remove(cardToPlay));
            
             turnState = turnState.withNewCardPlayed(cardToPlay);
            
             // UPDATE THE HAND
             hands.replace(p, hands.get(p).remove(cardToPlay));
             }
             players.get(PlayerId.PLAYER_1).updateTrick(turnState.trick());
    }
 

    // // The trick has not started.
    // if (isGameOver()) {
    // } else {
    // if (!turnState.isTerminal() && turnState.trick().isFull()) {
    // turnState = turnState.withTrickCollected();
    // }
    // if (turnState.isTerminal() || turnState.trick().index() == 0 ) {
    // deal();
    // if (turnState.isTerminal()) {
    // organizePlayers(turnState.nextPlayer());
    // turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
    // turnState.score().nextTurn(), turnState.nextPlayer());
    // }else {
    // organizePlayers(firstPlayer());
    // turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
    // Score.INITIAL, firstPlayer());
    // }
    // for (PlayerId p : playersInOrder) {
    // players.get(p).setPlayers(PlayerId.PLAYER_1, playerNames);
    // players.get(p).updateHand(hands.get(PlayerId.PLAYER_1));
    // players.get(p).setTrump(turnState.trick().trump());
    // }
    // }
    // for (PlayerId p : playersInOrder) {
    // players.get(p).updateScore(turnState.score());
    // }
    //
    // for (PlayerId p : playersInOrder) {
    //
    // for (PlayerId q : playersInOrder) {
    // players.get(q).updateTrick(turnState.trick());
    // }
    //
    // Card cardToPlay = players.get(p).cardToPlay(turnState,
    // hands.get(p));
    //
    // players.get(p).updateHand(hands.get(p).remove(cardToPlay));
    //
    // turnState = turnState.withNewCardPlayed(cardToPlay);
    //
    // // UPDATE THE HAND
    // hands.replace(p, hands.get(p).remove(cardToPlay));
    // }
    // players.get(PlayerId.PLAYER_1).updateTrick(turnState.trick());
    //
    // }
    //

    private void deal() {

        List<Card> cards = new LinkedList<Card>();
        // Initialization of the list of cards.
        for (int i = 0; i < 4; i++) {
            for (int j = 0; j < 9; j++) {
                cards.add(Card.ofPacked(j + (i << 4)));
            }
        }
        Collections.shuffle(cards, shuffleRng);
        for (PlayerId p : PlayerId.ALL) {
            CardSet hand = CardSet.EMPTY;
            for (int i = CARDS_PER_HAND * p.ordinal(); i < CARDS_PER_HAND
                    * (p.ordinal() + 1); i++) {
                hand = hand.add(cards.get(i));
            }
            hands.put(p, hand);
        }

    }

    private PlayerId firstPlayer() {

        for (PlayerId p : hands.keySet()) {
            if (hands.get(p).contains(Card.of(Color.DIAMOND, Rank.SEVEN))) {
                return p;
            }

        }
        // This shouldn't happen
        return PlayerId.PLAYER_1;
    }

    private void organizePlayers(PlayerId firstPlayer) {
        playersInOrder.clear();
        for (int i = firstPlayer.ordinal(); i < firstPlayer.ordinal()
                + 4; i++) {
            playersInOrder.add(PlayerId.values()[i % 4]);
        }
        // for(PlayerId p: playersInOrder)
        // System.out.println(p);
    }

    private PlayerId turnStarter() {
        if (turnState.score() == Score.INITIAL) {
            turnStarter = firstPlayer();
        } else {
            turnStarter = PlayerId.values()[(turnStarter.ordinal() + 1) % 4];
        }
        return turnStarter;
    }
}
