/*
 *  Author : Alexandre Santangelo & Jonathan Bereyziat
 *  Date   : Apr 29, 2019   
*/
package ch.epfl.javass.gui;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class that define the graphical interface for a Jass human player
 */
public class GraphicalPlayer {

    private static final ObservableMap<Card, Image> cards = mapCreator(240);
    private static final ObservableMap<Color, Image> trumps = trumps();
    private static final int CARD_WIDTH = 120;
    private static final int CARD_HEIGHT = 180;
    private static final int HANDCARD_WIDTH = 80;
    private static final int HANDCARD_HEIGHT = 120;

    private final Scene scene;
    private final String player;

    /**
     * Create the different panes and place it into the scene
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> names,
            TrickBean trick, ScoreBean score, HandBean hand,
            ArrayBlockingQueue<Card> cardToPlay) {
        this.player = names.get(player);
        BorderPane borderPane = new BorderPane();
        borderPane.setCenter(createTrickPane(trick, player, names));
        borderPane.setTop(createScorePane(score, names));
        borderPane.setBottom(createHandPane(hand, player, cardToPlay));
        StackPane mainPane = new StackPane();
        mainPane.getChildren().add(borderPane); 
        mainPane.getChildren().addAll(createWinningPane(score,names)); 
        scene = new Scene(mainPane);
    }

    /**
     * Creates the Stage in which the scene takes place
     */
    public Stage createStage() {
        Stage stage = new Stage();
        stage.setTitle("Javass - " + player);
        stage.setScene(scene);
        return stage;
    }

    /**
     * Creates the Pane in which the score is displayed
     */
    private GridPane createScorePane(ScoreBean score,
            Map<PlayerId, String> map) {
        GridPane scorePane = new GridPane();
        StringProperty diff1 = new SimpleStringProperty();
        StringProperty diff2 = new SimpleStringProperty();

        score.turnPointsProperty(TeamId.TEAM_1).addListener((o, oV, nV) -> {
            int diffInt = nV.intValue() - oV.intValue();
            IntegerProperty diff = new SimpleIntegerProperty(
                    diffInt < 0 ? 0 : diffInt);
            diff1.bind(Bindings.concat("(+", Bindings.convert(diff), ")"));
        });
        score.turnPointsProperty(TeamId.TEAM_2).addListener((o, oV, nV) -> {
            int diffInt = nV.intValue() - oV.intValue();
            IntegerProperty diff = new SimpleIntegerProperty(
                    diffInt < 0 ? 0 : diffInt);
            diff2.bind(Bindings.concat("(+", Bindings.convert(diff), ")"));
        });

        Text names1 = new Text(map.get(PlayerId.PLAYER_1) + " et "
                + map.get(PlayerId.PLAYER_3) + " : ");
        Text names2 = new Text(map.get(PlayerId.PLAYER_2) + " et "
                + map.get(PlayerId.PLAYER_4) + " : ");

        Text turnPoints1 = new Text();
        turnPoints1.textProperty().bind(
                Bindings.convert(score.turnPointsProperty(TeamId.TEAM_1)));
        Text turnPoints2 = new Text();
        turnPoints2.textProperty().bind(
                Bindings.convert(score.turnPointsProperty(TeamId.TEAM_2)));

        Text difference1 = new Text();
        difference1.textProperty().bind(diff1);
        Text difference2 = new Text();
        difference2.textProperty().bind(diff2);

        Text total1 = new Text("/Total : ");
        Text total2 = new Text("/Total : ");
        Text gamePoints1 = new Text();
        gamePoints1.textProperty().bind(
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_1)));
        Text gamePoints2 = new Text();
        gamePoints2.textProperty().bind(
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_2)));

        scorePane.addRow(0, names1);
        scorePane.addRow(0, turnPoints1);
        scorePane.addRow(0, difference1);
        scorePane.addRow(0, total1);
        scorePane.addRow(0, gamePoints1);

        scorePane.addRow(1, names2);
        scorePane.addRow(1, turnPoints2);
        scorePane.addRow(1, difference2);
        scorePane.addRow(1, total2);
        scorePane.addRow(1, gamePoints2);

        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray;-fx-padding: 5px; -fx-alignment: center;");
        return scorePane;
    }

    /**
     * Creates the pane in which the Trick is displayed
     */
    private GridPane createTrickPane(TrickBean trick, PlayerId player,
            Map<PlayerId, String> map) {
        GridPane trickPane = new GridPane();
        VBox[] pairs = new VBox[4];
        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty()
                .bind(Bindings.valueAt(trumps, trick.ColorProperty()));
        trumpImage.setFitHeight(101);
        trumpImage.setFitWidth(101);

        for (int i = 0; i < PlayerId.COUNT; i++) {
            pairs[i] = trickCard(trick, player, i, map);
        }
        trickPane.add(pairs[0], 1, 2, 1, 1);
        trickPane.add(pairs[1], 2, 0, 1, 3);
        trickPane.add(pairs[2], 1, 0, 1, 1);
        trickPane.add(pairs[3], 0, 0, 1, 3);
        trickPane.add(trumpImage, 1, 1, 1, 1);
        GridPane.setHalignment(trumpImage, HPos.CENTER);
        trickPane.setStyle(TRICK_STYLE);

        return trickPane;
    }

    private VBox trickCard(TrickBean trick, PlayerId player, int i,
            Map<PlayerId, String> map) {
        Rectangle rect = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        rect.setStyle(RECT_STYLE);
        rect.setEffect(new GaussianBlur(4));
        ObjectBinding<Card> card = Bindings.valueAt(trick.trick(),
                PlayerId.values()[(player.ordinal() + i) % 4]);
        ImageView cardImage = new ImageView();
        cardImage.imageProperty().bind(Bindings.valueAt(cards, card));
        cardImage.setFitWidth(CARD_WIDTH);
        cardImage.setFitHeight(CARD_HEIGHT);
        StackPane pane = new StackPane(rect, cardImage);
        rect.visibleProperty()
                .bind(trick.winningPlayerProperty()
                        .isEqualTo(
                                PlayerId.values()[(player.ordinal() + i) % 4])
                        .and(cardImage.imageProperty().isNotNull()));
        Text name = new Text(
                map.get(PlayerId.values()[(player.ordinal() + i) % 4]));
        name.setStyle("-fx-font: 14 Optima;");
        VBox pair;
        if (i != 0)
            pair = new VBox(name, pane);
        else
            pair = new VBox(pane, name);
        pair.setStyle("-fx-padding: 5px; -fx-alignment: center;");

        return pair;
    }





    /**
     * Creates the Pane that will be displayed at the end of the game
     */
    private List<BorderPane> createWinningPane(ScoreBean score,
            Map<PlayerId, String> map) {
        List<BorderPane> winningPane = new ArrayList<>();
        BorderPane[] teamPane = new BorderPane[TeamId.COUNT];
        Text[] teamText = new Text[TeamId.COUNT];
        teamText[0] = new Text();
        teamText[1] = new Text();
        teamText[0].textProperty()
                .bind(Bindings.format(map.get(PlayerId.PLAYER_1) + " et "
                        + map.get(PlayerId.PLAYER_3) + " ont gagné avec "
                        + score.gamePointsProperty(TeamId.TEAM_1)
                        + " points contre "
                        + score.gamePointsProperty(TeamId.TEAM_2)));
        teamText[1].textProperty()
                .bind(Bindings.format(map.get(PlayerId.PLAYER_2) + " et "
                        + map.get(PlayerId.PLAYER_4) + " ont gagné avec "
                        + score.gamePointsProperty(TeamId.TEAM_2)
                        + " points contre "
                        + score.gamePointsProperty(TeamId.TEAM_1)));
        for (int i = 0; i < TeamId.COUNT; i++) {
            teamText[i].setStyle(TEXT_STYLE);
            teamPane[i] = new BorderPane();
            teamPane[i].setStyle(
                    "-fx-font: 16 Optima; -fx-background-color: white;");
            teamPane[i].visibleProperty().bind(
                    score.winningTeamProperty().isEqualTo(TeamId.values()[i]));
            teamPane[i].setCenter(teamText[i]);
            winningPane.add(teamPane[i]);
        }

        return winningPane;
    }

    /**
     * Creates the pane in which the Hand of the Player will be displayed
     */
    private HBox createHandPane(HandBean hand, PlayerId player,
            ArrayBlockingQueue<Card> cardQueue) {
        HBox handBox = new HBox();
        ImageView cardImages[] = new ImageView[9];
        for (int i = 0; i < cardImages.length; i++) {
            cardImages[i] = createHandCard(i, hand, cardQueue);
        }
        handBox.getChildren().addAll(cardImages);
        handBox.setStyle("-fx-background-color: lightgray;\r\n"
                + "-fx-spacing: 5px;\r\n" + "-fx-padding: 5px;");
        return handBox;
    }

    /**
     * Creates a Card view with some properties
     */
    private ImageView createHandCard(int i, HandBean hand,
            ArrayBlockingQueue<Card> cardQueue) {
        ImageView cardImage = new ImageView();
        cardImage.imageProperty().bind(
                Bindings.valueAt(cards, Bindings.valueAt(hand.hand(), i)));
        cardImage.setFitWidth(HANDCARD_WIDTH);
        cardImage.setFitHeight(HANDCARD_HEIGHT);
        BooleanProperty isPlayable = new SimpleBooleanProperty(true);
        isPlayable.bind(Bindings.createBooleanBinding(
                () -> hand.playableCards().contains(hand.hand().get(i)),
                hand.playableCards(), hand.hand()));
        cardImage.opacityProperty()
                .bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
        cardImage.disableProperty()
                .bind(Bindings.when(isPlayable).then(false).otherwise(true));
        cardImage.setOnMouseClicked(e -> {
            try {
                cardQueue.put(hand.hand().get(i));
            } catch (InterruptedException e1) {
                throw new Error(e1);
            }
        });
        return cardImage;
    }

    /**
     * Creates the map of card images
     */
    private static final ObservableMap<Card, Image> mapCreator(int quality) {
        assert (quality == 240 || quality == 160);

        ObservableMap<Card, Image> map = FXCollections.observableHashMap();
        for (Card.Color c : Card.Color.ALL) {
            for (Card.Rank r : Card.Rank.ALL) {
                map.put(Card.of(c, r), new Image("/card_" + c.ordinal() + "_"
                        + r.ordinal() + "_" + quality + ".png"));
            }
        }
        return map;
    }

    /**
     * Creates the map of trump images
     */
    private static final ObservableMap<Color, Image> trumps() {
        ObservableMap<Color, Image> map = FXCollections.observableHashMap();

        for (Card.Color c : Card.Color.ALL)
            map.put(c, new Image("/trump_" + c.ordinal() + ".png"));

        return map;
    }

}
