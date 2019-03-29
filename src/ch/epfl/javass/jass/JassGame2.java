package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

public final class JassGame2 {
    private Random shuffleRng;
    private Random trumpRng;

    private Color trump;

    private Map<PlayerId, Player> players;
    private Map<PlayerId, CardSet> handPlayer;
    private PlayerId firstPlayerOfTurn;

    private TurnState state;
    private Score score;

    public JassGame2(long rngSeed, Map<PlayerId, Player> players, Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        score = Score.INITIAL;

        handPlayer = new HashMap<PlayerId, CardSet>();

        this.players = Collections.unmodifiableMap(new HashMap<>(players));

        state = TurnState.ofPackedComponents(0, PackedCardSet.ALL_CARDS, 0);

        for (PlayerId player : PlayerId.ALL) {
            players.get(player).setPlayers(player, playerNames);
        }

        nextTurn();
        state = TurnState.initial(trump, score, firstPlayerOfTurn);
        updateScoreForPlayers();
        updateTrickForPlayers();
    }

    private void nextTurn() {
        List<Card> deck = new ArrayList<Card>();
        for (int i = 0; i < CardSet.ALL_CARDS.size(); ++i) {
            deck.add(CardSet.ALL_CARDS.get(i));
        }
        Collections.shuffle(deck, shuffleRng);

        trump = Color.ALL.get(trumpRng.nextInt(Color.ALL.size()));

        handPlayer.clear();

        int index = 0;
        for (PlayerId player : PlayerId.ALL) {
            List<Card> newHand = new ArrayList<Card>();
            for (int i = 0; i < 9; ++i) {
                newHand.add(deck.get(i + index));
            }

            players.get(player).setTrump(trump);
            players.get(player).updateHand(CardSet.of(newHand));
            handPlayer.put(player, CardSet.of(newHand));
            index += 9;

            if (newHand.contains(Card.of(Color.DIAMOND, Rank.SEVEN)) && state.score().totalPoints(TeamId.TEAM_1) == 0
                    && state.score().totalPoints(TeamId.TEAM_2) == 0) {
                firstPlayerOfTurn = player;
            }
        }
        if (state.score().totalPoints(TeamId.TEAM_1) +  state.score().totalPoints(TeamId.TEAM_2) != 0) {
            int ordinal = (firstPlayerOfTurn.ordinal() + 1) % 4;
            System.out.println(ordinal);
            firstPlayerOfTurn = PlayerId.ALL.get((ordinal >= 0) ? ordinal : ordinal + 4);
        }
    }

    public boolean isGameOver() {
        if (state.score().totalPoints(TeamId.TEAM_1) >= Jass.WINNING_POINTS) {
            System.out.println("And The Winner Is...... TEAM_1");
            for (PlayerId player : PlayerId.values()) {
                players.get(player).setWinningTeam(TeamId.TEAM_1);
            }
            return true;
        } else if (state.score().totalPoints(TeamId.TEAM_2) >= Jass.WINNING_POINTS) {
            System.out.println("And The Winner Is...... TEAM_2");
            for (PlayerId player : PlayerId.values()) {
                players.get(player).setWinningTeam(TeamId.TEAM_2);
            }
            return true;
        }
        return false;
    }

    public void advanceToEndOfNextTrick() {
        if (isGameOver()) {
            return;
        }
        if (state.trick().isFull()) {
            state = state.withTrickCollected();
            updateScoreForPlayers();
            updateTrickForPlayers();
        }
        if (state.isTerminal()) {
            nextTurn();
            state = TurnState.initial(trump, state.score().nextTurn(), firstPlayerOfTurn);
        }
        for (int i = 0; i < 4; ++i) {
            PlayerId nextPlayer = state.nextPlayer();
            makePlayerPlay(nextPlayer);
        }
    }

    private void makePlayerPlay(PlayerId player) {
        Card cardPlayed = players.get(player).cardToPlay(state, handPlayer.get(player));

        this.state = state.withNewCardPlayed(cardPlayed);

        CardSet newCardSet = handPlayer.get(player).remove(cardPlayed);

        handPlayer.put(player, newCardSet);
        players.get(player).updateHand(newCardSet);

        updateTrickForPlayers();
        updateScoreForPlayers();
    }

    private void updateTrickForPlayers() {
        for (PlayerId player : PlayerId.ALL) {
            players.get(player).updateTrick(state.trick());
        }
    }

    private void updateScoreForPlayers() {
        for (PlayerId player : PlayerId.ALL) {
            players.get(player).updateScore(state.score());
        }
    }
}