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
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
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
import javafx.geometry.Bounds;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;

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
    private static final String TEXT_STYLE = "-fx-font: 16 Optima; -fx-background-color: lightgray;-fx-padding: 5px; -fx-alignment: center;";
    private static final String RECT_STYLE = "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;";
    private static final String TRICK_STYLE = "-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center; ";
    private static final String HANDBOX_STYLE = "-fx-background-color: lightgray;\r\n-fx-spacing: 5px;\r\n-fx-padding: 5px;";

    private final Scene scene;
    //TODO En attribu ou pas ?
    private final String player;
    private double xCardPos;
    private double yCardPos;

    /**
     * Create the different panes and place it into the scene
     */
    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> names,
            TrickBean trick, ScoreBean score, HandBean hand,
            ArrayBlockingQueue<Card> cardToPlay, CardBean cardBean) {
        this.player = names.get(player);
        BorderPane borderPane = new BorderPane();
        Button b = new Button("Help me !");
        borderPane.setCenter(createTrickPane(trick, player, names));
        //TODO : Attribus de classe ou argument ?
        borderPane.setBottom(
                createHandPane(hand, player, cardToPlay, cardBean, b));
        borderPane.setTop(createScorePane(score, names));
        StackPane mainPane = new StackPane();
        mainPane.getChildren().add(borderPane);
        mainPane.getChildren().add(b);
        StackPane.setAlignment(b, Pos.CENTER_RIGHT);
        mainPane.getChildren().addAll(createWinningPane(score, names));
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
        Text[] teamTexts = new Text[8];
        for (int i = 0; i < 8; i++)
            teamTexts[i] = new Text();
        StringProperty differences[] = new StringProperty[2];
        for (TeamId t : TeamId.ALL) {
            differences[t.ordinal()] = new SimpleStringProperty();
            score.turnPointsProperty(t).addListener((o, oV, nV) -> {

                int diffInt = nV.intValue() - oV.intValue();
                IntegerProperty diff = new SimpleIntegerProperty(
                        diffInt < 0 ? 0 : diffInt);
                differences[t.ordinal()].bind(
                        Bindings.concat("(+", Bindings.convert(diff), ")"));
            });
            teamTexts[2 + t.ordinal()].textProperty()
                    .bind(Bindings.convert(score.turnPointsProperty(t)));

            teamTexts[4 + t.ordinal()].textProperty()
                    .bind(differences[t.ordinal()]);

            teamTexts[6 + t.ordinal()].textProperty()
                    .bind(Bindings.convert(score.gamePointsProperty(t)));
        }
        //TODO peut mieux faire
        teamTexts[0] = new Text(map.get(PlayerId.PLAYER_1) + " et "
                + map.get(PlayerId.PLAYER_3) + " : ");
        teamTexts[1] = new Text(map.get(PlayerId.PLAYER_2) + " et "
                + map.get(PlayerId.PLAYER_4) + " : ");
        for (int j = 0; j < TeamId.COUNT; j++)
            for (int i = 0; i < 4; i++) {
                if (i == 3)
                    scorePane.addRow(j, new Text("/Total : "));
                scorePane.addRow(j, teamTexts[2 * i + j]);
            }

        scorePane.setStyle(TEXT_STYLE);
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
            //TODO mieux de faire if ou de hardCoder ?
            if (i % 2 == 0)
                trickPane.add(pairs[i], 1, 2 - i);
            else
                trickPane.add(pairs[i], 3 - i, 0, 1, 3);
        }
//        trickPane.add(pairs[0], 1, 2);
//        trickPane.add(pairs[1], 2, 0, 1, 3);
//        trickPane.add(pairs[2], 1, 0);
//        trickPane.add(pairs[3], 0, 0, 1, 3);
        trickPane.add(trumpImage, 1, 1, 1, 1);
        GridPane.setHalignment(trumpImage, HPos.CENTER);
        trickPane.setStyle(TRICK_STYLE);

        return trickPane;
    }

    private VBox trickCard(TrickBean trick, PlayerId player, int i,
            Map<PlayerId, String> map) {
        
        PlayerId cardPlayer = PlayerId.ALL.get((player.ordinal() + i) % PlayerId.COUNT);
        Rectangle rect = new Rectangle(CARD_WIDTH, CARD_HEIGHT);
        rect.setStyle(RECT_STYLE);
        rect.setEffect(new GaussianBlur(4));
        ObjectBinding<Card> card = Bindings.valueAt(trick.trick(), cardPlayer);
        ImageView cardImage = new ImageView();
        card.addListener((o, oV, nV) -> {
            imageThrowAnimation(cardImage, i);
        });

        cardImage.imageProperty().bind(Bindings.valueAt(cards, card));
        cardImage.setFitWidth(CARD_WIDTH);
        cardImage.setFitHeight(CARD_HEIGHT);
        StackPane pane = new StackPane(rect, cardImage);
        rect.visibleProperty()
                .bind(trick.winningPlayerProperty()
                        .isEqualTo(cardPlayer)
                        .and(cardImage.imageProperty().isNotNull()));
        Text name = new Text(
                map.get(cardPlayer));
        name.setStyle("-fx-font: 14 Optima;");
        VBox pair;
        
        if (i != 0)
            pair = new VBox(name, pane);
        else
            pair = new VBox(pane, name);
        pair.setStyle("-fx-padding: 5px; -fx-alignment: center;");
        return pair;
    }

    private void imageThrowAnimation(ImageView cardImage, int index ) {
        Timeline timeline = new Timeline();
        double travelH = scene.getWidth()/2;
        double travelV = scene.getHeight()/2;
        if (index == 0) {
            cardImage.setRotate(-100);
            Bounds newCardPos = cardImage.localToScene(cardImage.getBoundsInLocal());
            double xTrans = xCardPos-newCardPos.getMinX();
            double yTrans = yCardPos-newCardPos.getMinY();
            cardImage.setScaleX(HANDCARD_HEIGHT/CARD_HEIGHT);
            cardImage.setScaleY(HANDCARD_WIDTH/CARD_WIDTH);
            cardImage.setTranslateX(xTrans);
            cardImage.setTranslateY(yTrans);
        } else if (index == 1) {
            cardImage.setRotate(-100*travelH/385);
            cardImage.setTranslateX(travelH);
        } else if (index == 2) {
            cardImage.setTranslateY(-travelV);
        } else {
            cardImage.setRotate(100*travelH/385);
            cardImage.setTranslateX(-travelH);
        }
        timeline.getKeyFrames()
                .addAll(new KeyFrame(Duration.millis(500), "Throw",
                        new KeyValue(cardImage.rotateProperty(), 0),
                        new KeyValue(cardImage.scaleXProperty(), 1),
                        new KeyValue(cardImage.scaleYProperty(), 1),
                        new KeyValue(cardImage.translateXProperty(), 0),
                        new KeyValue(cardImage.translateYProperty(), 0)));
        timeline.play();

    }

    private List<BorderPane> createWinningPane(ScoreBean score,
            Map<PlayerId, String> map) {
        List<BorderPane> winningPane = new ArrayList<>();
        BorderPane[] teamPane = new BorderPane[TeamId.COUNT];
        Text[] teamText = new Text[TeamId.COUNT];
        teamText[0] = new Text();
        teamText[1] = new Text();

        teamText[0].textProperty().bind(Bindings.concat(
                map.get(PlayerId.PLAYER_1), " et ", map.get(PlayerId.PLAYER_3),
                " ont gagné avec ",
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_1)),
                " points contre ",
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_2))));
        teamText[1].textProperty().bind(Bindings.concat(
                map.get(PlayerId.PLAYER_2), " et ", map.get(PlayerId.PLAYER_4),
                " ont gagné avec ",
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_2)),
                " points contre ",
                Bindings.convert(score.gamePointsProperty(TeamId.TEAM_1))));

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
            ArrayBlockingQueue<Card> cardQueue, CardBean cardBean, Button b) {
        HBox handBox = new HBox();
        StackPane cardImages[] = new StackPane[9];
        for (int i = 0; i < cardImages.length; i++) {
            cardImages[i] = createHandCard(i, hand, cardQueue, cardBean, b);
        }
        handBox.getChildren().addAll(cardImages);
        handBox.setStyle(HANDBOX_STYLE);
        return handBox;
    }

    private StackPane createHandCard(int i, HandBean hand,
            ArrayBlockingQueue<Card> cardQueue, CardBean cardBean, Button b) {
        ImageView cardImage = new ImageView();
        Rectangle selectZone = new Rectangle(HANDCARD_WIDTH, HANDCARD_HEIGHT);
        selectZone.setFill(javafx.scene.paint.Color.TRANSPARENT);
        Circle greenCircle = new Circle(4, javafx.scene.paint.Color.LIMEGREEN);
        StackPane card = new StackPane(cardImage, greenCircle,selectZone);
        StackPane.setAlignment(greenCircle, Pos.TOP_RIGHT);
        cardImage.imageProperty().bind(
                Bindings.valueAt(cards, Bindings.valueAt(hand.hand(), i)));
        cardImage.setFitWidth(HANDCARD_WIDTH);
        cardImage.setFitHeight(HANDCARD_HEIGHT);
        cardImage.setTranslateX(0);
        cardImage.setTranslateY(0);
        greenCircle.visibleProperty().bind(Bindings.valueAt(hand.hand(), i)
                .isEqualTo(cardBean.card()).and(b.armedProperty()));
        BooleanProperty isPlayable = new SimpleBooleanProperty(true);
        cardImage.imageProperty().addListener((o, oV, nV) -> {
            card.setTranslateY(-HANDCARD_HEIGHT * 3);
            card.setScaleX(0);
            card.setScaleY(0);
            card.setRotate(360);
            Timeline timeline = new Timeline();
            timeline.getKeyFrames().addAll(
                    new KeyFrame(
                            Duration.millis(
                                    1000 / Math.log(Jass.HAND_SIZE - i + 1)),
                            "Translation",
                            new KeyValue(card.translateYProperty(), 0)),

                    new KeyFrame(Duration.millis(1500 / (Jass.HAND_SIZE - i)),
                            "Bigger", new KeyValue(card.scaleXProperty(), 1),
                            new KeyValue(card.scaleYProperty(), 1),
                            new KeyValue(card.rotateProperty(), 0)));
            timeline.play();
            timeline.setOnFinished(event -> {
                isPlayable.bind(Bindings.createBooleanBinding(
                        () -> hand.playableCards().contains(hand.hand().get(i)),
                        hand.playableCards(), hand.hand()));
                card.opacityProperty()
                        .bind(Bindings.when(isPlayable).then(1).otherwise(0.2));
                card.disableProperty().bind(
                        Bindings.when(isPlayable).then(false).otherwise(true));
                selectZone.setOnMouseEntered(e -> {
                    Timeline tl = new Timeline();
                    tl.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(50), "Translation",
                                    new KeyValue(cardImage.translateYProperty(),
                                            -HANDCARD_HEIGHT / 7)));
                    tl.play();
                });
                selectZone.setOnMouseExited(e -> {
                    Timeline tl = new Timeline();
                    tl.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(50), "Translation",
                                    new KeyValue(cardImage.translateYProperty(), 0)));
                    tl.play();
                });
                card.setOnMouseClicked(e -> {
                    Bounds cardPosition = cardImage.localToScene(cardImage.getBoundsInLocal());
                    xCardPos = cardPosition.getMinX();
                    yCardPos = cardPosition.getMinY();
                    try {
                        cardQueue.put(hand.hand().get(i));
                    } catch (InterruptedException e1) {
                        throw new Error(e1);
                    }
                });
            });

        });
        return card;
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
