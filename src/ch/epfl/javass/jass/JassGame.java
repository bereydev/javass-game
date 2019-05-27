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
    private static final int SEVEN_DIAMOND = PackedCard.pack(Color.DIAMOND,
            Rank.SEVEN);
    private Boolean newGame = true;
    private TurnState turnState;
    private Color trump; 

    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        shuffleRng = new Random(rng.nextLong());
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
        boolean b = turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                || turnState.score()
                        .totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS;
        if(b)
            System.out.println(turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS ? "////////////////////////////////////////": "Team2");
        return b; 
    }

    private void finalPlayerUpdate() {
        TeamId winningTeam = turnState.score()
                .totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS
                        ? TeamId.TEAM_1
                        : TeamId.TEAM_2;
        for (PlayerId p : PlayerId.ALL) {
            players.get(p).setWinningTeam(winningTeam);
            players.get(p).updateScore(turnState.score().nextTurn());
        }
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
            for (PlayerId p : PlayerId.ALL) {
                players.get(p).setPlayers(p, playerNames);
                players.get(p).updateHand(hands.get(p));
                players.get(p).updateScore(turnState.score());
            }
            PlayerId temp = turnStarter(); 
            trump = players.get(temp).trumpToPlay(hands.get(temp)); 
            turnState = TurnState.initial(trump, Score.INITIAL, temp);
            for (PlayerId p : PlayerId.ALL) 
                players.get(p).setTrump(turnState.trick().trump());
            
            newGame = false;
        } else {
            turnState = turnState.withTrickCollected();
            if (turnState.isTerminal()) {
                deal();
                for (PlayerId p : PlayerId.ALL) {
                    players.get(p).updateHand(hands.get(p));
                    players.get(p).updateScore(turnState.score());
                }
                PlayerId temp = turnStarter(); 
                trump = players.get(temp).trumpToPlay(hands.get(temp)); 
                turnState = TurnState.initial(trump,
                        turnState.score().nextTurn(), temp);
                for (PlayerId p : PlayerId.ALL) {
                    players.get(p).setTrump(turnState.trick().trump());
                }
            }
        }
        for (PlayerId p : PlayerId.ALL) {
            players.get(p).updateTrick(turnState.trick());
            players.get(p).updateScore(turnState.score());
        }
        if (isGameOver()) {
            finalPlayerUpdate();
            return;
        }
        for (int i = 0; i < PlayerId.COUNT; i++) {
            playTrick(turnState.nextPlayer());
        }
    }
    

    private void playTrick(PlayerId player) {
        players.get(player).updateScore(turnState.score());
        Card cardToPlay = players.get(player).cardToPlay(turnState,
                hands.get(player));
        hands.replace(player, hands.get(player).remove(cardToPlay));
        players.get(player).updateHand(hands.get(player));
        turnState = turnState.withNewCardPlayed(cardToPlay);
        for (PlayerId p : PlayerId.ALL)
            players.get(p).updateTrick(turnState.trick());
        players.get(player).updateScore(turnState.score());
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
        for (PlayerId p : PlayerId.ALL) {
            CardSet hand = CardSet.EMPTY;
            hand = CardSet
                    .of(cards.subList(p.ordinal() * 9, (p.ordinal() + 1) * 9));
            hands.put(p, hand);
        }
    }

    /**
     * @return The player that has the seven of diamonds.
     */
    private PlayerId firstPlayer() {
        PlayerId firstPlayer = PlayerId.PLAYER_1;
        for (PlayerId p : hands.keySet()) {
            if (PackedCardSet.contains(hands.get(p).packed(), SEVEN_DIAMOND))
                firstPlayer = p;
        }
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
