package ch.epfl.javass.gui;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import javafx.application.Platform;

public class GraphicalPlayerAdapter implements Player {

    private HandBean handBean;
    private ScoreBean scoreBean;
    private TrickBean trickBean;
    private CardBean cardBean; 
    private GraphicalPlayer graphicalPlayer;
    private ArrayBlockingQueue<Card> cardQueue;
    private ArrayBlockingQueue<Color> trumpQueue;
    private MctsPlayer helper; 

    public GraphicalPlayerAdapter() {
        handBean = new HandBean();
        scoreBean = new ScoreBean();
        trickBean = new TrickBean();
        cardQueue = new ArrayBlockingQueue<>(1);
        trumpQueue = new ArrayBlockingQueue<>(1);
        cardBean = new CardBean(); 
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Platform.runLater(() -> {
            CardSet playableCards = state.trick().playableCards(hand);
            handBean.setPlayableCards(playableCards);
            //BONUS
            cardBean.setCard(helper.cardToPlay(state, hand));
        });
        Card cardToPlay;
        try {
            cardToPlay = cardQueue.take();
            Platform.runLater(() -> {
                CardSet playableCards = CardSet.EMPTY;
                handBean.setPlayableCards(playableCards);
            });
            return cardToPlay;
        } catch (InterruptedException e) {
            throw new Error(e);
        }
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("eee");
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, trickBean,
                scoreBean, handBean, cardQueue,cardBean, trumpQueue);
        System.out.println("graph done");
        //BONUS
        helper = new MctsPlayer(ownId,0,10_000); 
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

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#trumpToPlay(ch.epfl.javass.jass.Card.Color, ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color trumpToPlay(CardSet hand) {
        trickBean.setNewTurn(true);
        Color trump = null; 
        try {
            trump = trumpQueue.take();
        } catch (InterruptedException e2) {
            System.err.println("WHAT");
            throw new Error(e2);
        }
        trickBean.setNewTurn(false);
        return trump;
    }
}
