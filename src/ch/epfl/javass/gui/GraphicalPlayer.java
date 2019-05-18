/*
 *  Author : Alexandre Santangelo 
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
    private final String player;

    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> names,
            TrickBean trick, ScoreBean score, HandBean hand,
            ArrayBlockingQueue<Card> cardToPlay, CardBean cardBean) {
        this.player = names.get(player);
        BorderPane borderPane = new BorderPane();
        Button b = new Button("Help me !");
        borderPane.setCenter(createTrickPane(trick, player, names));
        borderPane.setTop(createScorePane(score, names));
        borderPane.setBottom(
                createHandPane(hand, player, cardToPlay, cardBean, b));
        StackPane mainPane = new StackPane();
        mainPane.getChildren().add(borderPane);
        mainPane.getChildren().add(b);
        StackPane.setAlignment(b, Pos.CENTER_RIGHT);
        mainPane.getChildren().addAll(createWinningPane(score, names));

        scene = new Scene(mainPane);
    }

    public Stage createStage() {
        Stage stage = new Stage();
        stage.setTitle("Javass - " + player);
        stage.setScene(scene);
        return stage;
    }

    private GridPane createScorePane(ScoreBean score,
            Map<PlayerId, String> map) {
        GridPane scorePane = new GridPane();
        Text[] teamTexts = new Text[8];
        for(int i=0; i<8; i++) 
            teamTexts[i]= new Text(); 
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
            teamTexts[2+t.ordinal()].textProperty().bind(
                    Bindings.convert(score.turnPointsProperty(t)));
            
            teamTexts[4+t.ordinal()].textProperty().bind(differences[t.ordinal()]);
            
            teamTexts[6+t.ordinal()].textProperty().bind(
                    Bindings.convert(score.gamePointsProperty(t)));
        }

        teamTexts[0] = new Text(map.get(PlayerId.PLAYER_1) + " et "
                + map.get(PlayerId.PLAYER_3) + " : ");
        teamTexts[1]= new Text(map.get(PlayerId.PLAYER_2) + " et "
                + map.get(PlayerId.PLAYER_4) + " : ");
        for(int j = 0; j<TeamId.COUNT; j++)
            for(int i=0; i<4; i++) {
                if(i==3)
                    scorePane.addRow(j, new Text("/Total : "));
                scorePane.addRow(j, teamTexts[2*i + j]);
            }

        scorePane.setStyle(TEXT_STYLE);
        return scorePane;
    }

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
        imageThrowAnimation(cardImage, i);
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

    private void imageThrowAnimation(ImageView cardImage, int index) {
        cardImage.imageProperty().addListener((o, oV, nV) -> {
            Timeline timeline = new Timeline();
            if (index == 0) {
                cardImage.setRotate(360);
                cardImage.setTranslateY(400);
            } else if (index == 1) {
                cardImage.setRotate(-360);
                cardImage.setTranslateX(400);
            } else if (index == 2) {
                cardImage.setRotate(360);
                cardImage.setTranslateY(-400);
            } else {
                cardImage.setRotate(360);
                cardImage.setTranslateX(-400);
            }
            timeline.getKeyFrames()
                    .addAll(new KeyFrame(Duration.millis(500), "Throw",
                            new KeyValue(cardImage.rotateProperty(), 0),
                            new KeyValue(cardImage.translateXProperty(), 0),
                            new KeyValue(cardImage.translateYProperty(), 0)));
            timeline.play();
        });
    }

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
                        + score.gamePointsProperty(TeamId.TEAM_1).intValue()
                        + " points contre "
                        + score.gamePointsProperty(TeamId.TEAM_2).intValue()));
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
        Circle greenCircle = new Circle(4, javafx.scene.paint.Color.LIMEGREEN);
        StackPane card = new StackPane(cardImage, greenCircle);
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
                card.setOnMouseEntered(e -> {
                    Timeline tl = new Timeline();
                    tl.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(300), "Translation",
                                    new KeyValue(card.translateYProperty(),
                                            -HANDCARD_HEIGHT /3)),

                            new KeyFrame(Duration.millis(300), "Bigger",
                                    new KeyValue(card.scaleXProperty(), 1.5),
                                    new KeyValue(card.scaleYProperty(), 1.5)));
                    tl.play();
                });
                card.setOnMouseExited(e -> {
                    Timeline tl = new Timeline();
                    tl.getKeyFrames().addAll(
                            new KeyFrame(Duration.millis(200), "Translation",
                                    new KeyValue(card.translateYProperty(), 0)),

                            new KeyFrame(Duration.millis(200), "Bigger",
                                    new KeyValue(card.scaleXProperty(), 1),
                                    new KeyValue(card.scaleYProperty(), 1)));
                    tl.play();
                });
                card.setOnMouseClicked(e -> {
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

    private static final ObservableMap<Color, Image> trumps() {
        ObservableMap<Color, Image> map = FXCollections.observableHashMap();

        for (Card.Color c : Card.Color.ALL)
            map.put(c, new Image("/trump_" + c.ordinal() + ".png"));

        return map;
    }

}
