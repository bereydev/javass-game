package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

/**
 * Class that create the stage and acts as a "Human" player
 */
public class GraphicalPlayerAdapter implements Player {

    private HandBean handBean;
    private ScoreBean scoreBean;
    private TrickBean trickBean;
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> cardQueue;

    public GraphicalPlayerAdapter() {
        handBean = new HandBean();
        scoreBean = new ScoreBean();
        trickBean = new TrickBean();
        cardQueue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> {
            CardSet playableCards = state.trick().playableCards(hand);
            handBean.setPlayableCards(playableCards);
        });
        Card cardToPlay;
        try {
            cardToPlay = cardQueue.take();
            Platform.runLater(() -> {
                // to disable when the player turn is finished
                handBean.setPlayableCards(CardSet.EMPTY);
            });
            return cardToPlay;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, trickBean,
                scoreBean, handBean, cardQueue);
        Platform.runLater(() -> {
            graphicalPlayer.createStage().show();
        });
    }

    @Override
    public void updateHand(CardSet newHand) {
        Platform.runLater(() -> {
            handBean.setHand(newHand);
        });
    }

    @Override
    public void setTrump(Color trump) {
        Platform.runLater(() -> {
            trickBean.setTrump(trump);
        });
    }

    @Override
    public void updateTrick(Trick newTrick) {
        Platform.runLater(() -> {
            trickBean.setTrick(newTrick);
        });
    }

    @Override
    public void updateScore(Score score) {
        Platform.runLater(() -> {
            for (TeamId team : TeamId.ALL) {
                scoreBean.setGamePoints(team, score.gamePoints(team));
                scoreBean.setTurnPoints(team, score.turnPoints(team));
                scoreBean.setTotalPoints(team, score.totalPoints(team));
            }
        });
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        Platform.runLater(() -> {
            scoreBean.setWinningTeam(winningTeam);
        });
    }
}
