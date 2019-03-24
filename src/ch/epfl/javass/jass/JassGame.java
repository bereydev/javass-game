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
    private List<PlayerId> playersTurn = new LinkedList<PlayerId>();
    private PlayerId turnStarter; 
    private Random shuffleRng;
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

    /**
     * @return True if the game is over (a team has 1000points)
     */

    public boolean isGameOver() {
        return turnState.score().totalPoints(TeamId.TEAM_1) >= 1200
                || turnState.score().totalPoints(TeamId.TEAM_2) >= 1200;
    }

    /**
     * Advances to the end of the next trick, doing everything.
     */

    public void advanceToEndOfNextTrick() {
        // The trick has not started.
        if (isGameOver()) {
            for (PlayerId p : playersInOrder) {
                players.get(p).updateScore(turnState.score());
            }
        } else {
            if (!turnState.isTerminal() && turnState.trick().isFull()) {
                turnState = turnState.withTrickCollected();
            }
            if (turnState.isTerminal() || turnState.trick().index() == 0 ) {
                deal();
                PlayerId starter = turnStarter(); 
                if (turnState.isTerminal()) {
                    turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                            turnState.score().nextTurn(), starter);
                }else {
                    turnState = TurnState.initial(Color.values()[trumpRng.nextInt(4)],
                            Score.INITIAL, starter);
                }
                
                organizePlayers(starter);
                for (PlayerId p : playersInOrder) {
                    players.get(p).setPlayers(PlayerId.PLAYER_1, playerNames);
                    players.get(p).updateHand(hands.get(PlayerId.PLAYER_1));
                    players.get(p).setTrump(turnState.trick().trump());
                }
             
                
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
            
            organizePlayers(turnState.trick().winningPlayer());
        }
        
    }

    private void deal() {

        List<Card> cards = new LinkedList<Card>();
        
        for(Card.Color c : Card.Color.ALL)
            for(Card.Rank r : Card.Rank.ALL) 
                cards.add(Card.of(c, r)); 

        hands.clear();
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
        if(turnState.score().totalPoints(TeamId.TEAM_1)==0 &&turnState.score().totalPoints(TeamId.TEAM_1)==0) {
            turnStarter = firstPlayer(); 
        }
        else {
            turnStarter = PlayerId.values()[(turnStarter.ordinal()+1)%4]; 
        }
        return turnStarter; 
    }
}
