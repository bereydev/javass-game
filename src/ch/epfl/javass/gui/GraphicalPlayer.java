/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 29, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.gui;

import java.util.Map;

import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.IntegerProperty;
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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GraphicalPlayer {

    private static final ObservableMap<Card, Image> cards = mapCreator(240);
    private static final ObservableMap<Color, Image> trumps = trumps();

    private Scene scene;

    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> map,
            TrickBean trick, ScoreBean score) {
        BorderPane borderPane = new BorderPane();
        StackPane winningPane = new StackPane();
        borderPane.setCenter(createTrickPane(trick, player, map));
        borderPane.setTop(createScorePane(score, map));
        winningPane.getChildren().add(borderPane);
        winningPane.getChildren().add(createWinningPane(score, map));

        scene = new Scene(winningPane);
    }

    public Stage createStage() {
        Stage stage = new Stage();
        stage.setScene(scene);
        return stage;
    }

    private GridPane createScorePane(ScoreBean score,
            Map<PlayerId, String> map) {
        GridPane scorePane = new GridPane();
        StringProperty diff1 = new SimpleStringProperty();
        StringProperty diff2 = new SimpleStringProperty();

        score.turnPointsProperty(TeamId.TEAM_1).addListener((o, oV, nV) -> {
            IntegerProperty diff = new SimpleIntegerProperty(
                    nV.intValue() - oV.intValue());
            diff1.bind(Bindings.concat("(+", Bindings.convert(diff), ")"));
        });
        score.turnPointsProperty(TeamId.TEAM_2).addListener((o, oV, nV) -> {
            IntegerProperty diff = new SimpleIntegerProperty(
                    nV.intValue() - oV.intValue());
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

    private GridPane createTrickPane(TrickBean trick, PlayerId player,
            Map<PlayerId, String> map) {
        GridPane trickPane = new GridPane();
        StackPane stack = new StackPane();

        ImageView[] cardImages = new ImageView[4];
        Text[] names = new Text[4];
        VBox[] pairs = new VBox[4];
        StackPane[] panes = new StackPane[4];
        ImageView trumpImage = new ImageView();
        trumpImage.imageProperty()
                .bind(Bindings.valueAt(trumps, trick.ColorProperty()));
        trumpImage.setFitHeight(101);
        trumpImage.setFitWidth(101);

        Rectangle rect = new Rectangle(120, 180);
        rect.setStyle(
                "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");
        rect.setEffect(new GaussianBlur(4));
        stack.getChildren().add(rect);

        for (int i = 0; i < PlayerId.COUNT; i++) {
            panes[i] = new StackPane(rect);
            panes[i].visibleProperty().bind(trick.winningPlayerProperty()
                    .isEqualTo(PlayerId.values()[(player.ordinal() + i) % 4]));
            ObjectBinding<Card> card = Bindings.valueAt(trick.trick(),
                    PlayerId.values()[(player.ordinal() + i) % 4]);
            cardImages[i] = new ImageView();
            cardImages[i].imageProperty().bind(Bindings.valueAt(cards, card));
            cardImages[i].setFitWidth(120);
            cardImages[i].setFitHeight(180);
            names[i] = new Text(
                    map.get(PlayerId.values()[(player.ordinal() + i) % 4]));
            names[i].setStyle("-fx-font: 14 Optima;");
            if (i != 0)
                pairs[i] = new VBox(names[i], cardImages[i]);
            else
                pairs[i] = new VBox(cardImages[i], names[i]);
            pairs[i].setStyle("-fx-padding: 5px; -fx-alignment: center;");
            stack.getChildren().add(panes[i]);
        }

        trickPane.add(pairs[0], 1, 2, 1, 1);
        trickPane.add(pairs[1], 2, 0, 1, 3);
        trickPane.add(pairs[2], 1, 0, 1, 1);
        trickPane.add(pairs[3], 0, 0, 1, 3);
        trickPane.add(trumpImage, 1, 1, 1, 1);
        GridPane.setHalignment(trumpImage, HPos.CENTER);
        trickPane.setStyle(
                "-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");
        stack.getChildren().add(trickPane);
        return trickPane;
    }

    private StackPane createWinningPane(ScoreBean score,
            Map<PlayerId, String> map) {
        StackPane winningPane = new StackPane();
        BorderPane[] teamPane = new BorderPane[TeamId.COUNT];
        Text[] teamText = new Text[TeamId.COUNT];
        teamText[0] = new Text();
        teamText[1] = new Text();
        teamText[0].textProperty()
                .bind(Bindings.format(
                        "-fx-font: 16 Optima;-fx-background-color: white;",
                        map.get(PlayerId.PLAYER_1) + " et "
                                + map.get(PlayerId.PLAYER_3) + "ont gagné avec "
                                + score.gamePointsProperty(TeamId.TEAM_1)
                                + " points contre "
                                + score.gamePointsProperty(TeamId.TEAM_2)));
        teamText[1].textProperty()
                .bind(Bindings.format(
                        "-fx-font: 16 Optima;-fx-background-color: white;",
                        map.get(PlayerId.PLAYER_2) + " et "
                                + map.get(PlayerId.PLAYER_4) + "ont gagné avec "
                                + score.gamePointsProperty(TeamId.TEAM_2)
                                + " points contre "
                                + score.gamePointsProperty(TeamId.TEAM_1)));
        for (int i = 0; i < TeamId.COUNT; i++) {
            teamPane[i] = new BorderPane();
            teamText[i].visibleProperty().bind(
                    score.winningTeamProperty().isEqualTo(TeamId.values()[i]));
            teamPane[i].setCenter(teamText[i]);
            winningPane.getChildren().add(teamPane[i]); 
        }

        return winningPane;
    }

    private HBox createHandPane(HandBean hand, PlayerId player,
            ArrayBlockingQueue<Card> cardQueue) {
        HBox handPane = new HBox();

        return handPane;
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
