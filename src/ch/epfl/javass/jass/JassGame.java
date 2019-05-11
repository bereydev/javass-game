/*
 *  Author : Alexandre Santangelo 
 *  Date   : Mar 13, 2019   
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

    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> hands = new HashMap<PlayerId, CardSet>();
    private PlayerId turnStarter;
    private final Random shuffleRng;
    private PlayerId player1 = PlayerId.PLAYER_1;
    private final Random trumpRng;
    private Boolean newGame = true;
    private TurnState turnState;

    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {

        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        this.players = Collections.unmodifiableMap(new EnumMap<>(players));
        this.playerNames = Collections
                .unmodifiableMap(new EnumMap<>(playerNames));

        // Initialization of turnState (to be modified later)
        turnState = TurnState.initial(Color.values()[0], Score.INITIAL,
                PlayerId.PLAYER_1);
    }

    /**
     * @return True if the game is over.
     */

    public boolean isGameOver() {
        boolean GameOver = turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                || turnState.score()
                        .totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS;

        if (GameOver) {
            TeamId winningTeam = turnState.score()
                    .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                            ? TeamId.TEAM_1
                            : TeamId.TEAM_2;
            for (PlayerId p : PlayerId.ALL) {
                players.get(p).setWinningTeam(winningTeam);
                players.get(p).updateScore(turnState.score().nextTurn());
            }
        }
        return GameOver;
    }

    /**
     * Advances to the end of the next trick, doing everything.
     */

    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {
            return;
        }
        if (newGame) {
            deal();
            player1 = turnStarter();
            newGame = false;
            turnState = TurnState.initial(
                    Color.values()[trumpRng.nextInt(Color.COUNT)],
                    Score.INITIAL, player1);
            for (PlayerId p : PlayerId.ALL) {
                players.get(p).setPlayers(p, playerNames);
                players.get(p).updateHand(hands.get(p));
                players.get(p).setTrump(turnState.trick().trump());
            }
        } else {
            turnState = turnState.withTrickCollected();
            if (turnState.isTerminal()) {
                deal();
                player1 = turnStarter();
                turnState = TurnState.initial(
                        Color.values()[trumpRng.nextInt(Color.COUNT)],
                        turnState.score().nextTurn(), player1);
                for (PlayerId p : PlayerId.ALL) {
                    players.get(p).updateHand(hands.get(p));
                    players.get(p).setTrump(turnState.trick().trump());
                }
            }
        }
        if (isGameOver()) {
            return;
        } 
        for (int i = 0; i < PlayerId.COUNT; i++ ) {
            players.get(turnState.nextPlayer()).updateScore(turnState.score());
            for (PlayerId q : PlayerId.ALL)
                players.get(q).updateTrick(turnState.trick());

            Card cardToPlay = players.get(turnState.nextPlayer()).cardToPlay(turnState,
                    hands.get(turnState.nextPlayer()));
            hands.replace(turnState.nextPlayer(), hands.get(turnState.nextPlayer()).remove(cardToPlay));
            players.get(turnState.nextPlayer()).updateHand(hands.get(turnState.nextPlayer()));

            turnState = turnState.withNewCardPlayed(cardToPlay);
        }
        for (PlayerId p : PlayerId.ALL)
            players.get(p).updateTrick(turnState.trick());

        player1 = turnState.trick().winningPlayer();

    }

    /**
     * Shuffles and distributes the card to each player
     */
    private void deal() {
        List<Card> cards = new LinkedList<Card>();

        for (int i = 0; i < CardSet.ALL_CARDS.size(); ++i) {
            cards.add(CardSet.ALL_CARDS.get(i));
        }
        Collections.shuffle(cards, shuffleRng);

        hands.clear();
        for (PlayerId p : players.keySet()) {
            CardSet hand = CardSet.EMPTY;
            for (int i = Jass.HAND_SIZE * p.ordinal(); i < Jass.HAND_SIZE
                    * (p.ordinal() + 1); i++) {
                hand = hand.add(cards.get(i));
            }
            hands.put(p, hand);
        }
    }

    /**
     * @return The player that has the seven of diamonds.
     */
    private PlayerId firstPlayer() {
        PlayerId firstPlayer = PlayerId.PLAYER_1;
        for (PlayerId p : hands.keySet()) {
            if (hands.get(p).contains(Card.of(Color.DIAMOND, Rank.SEVEN)))
                firstPlayer = p;
        }
        // This shouldn't happen
        return firstPlayer;
    }

    /**
     * @return The player that starts the turn.
     */
    private PlayerId turnStarter() {
        if (newGame)
            turnStarter = firstPlayer();
        else
            turnStarter = PlayerId.values()[(turnStarter.ordinal() + 1)
                    % PlayerId.COUNT];
        return turnStarter;
    }

}
