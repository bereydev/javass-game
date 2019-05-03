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
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
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

    private static final ObservableMap<Card, Image> images = mapCreator(240);

    public GraphicalPlayer(PlayerId player, Map<PlayerId, String> map,
            TrickBean trick, ScoreBean score) {
        BorderPane scorePane = new BorderPane();
        BorderPane trickPane = new BorderPane();
        StackPane winningPane = new StackPane();
        winningPane.getChildren().add(createWinningPane(score, map));
        trickPane.getChildren().add(createTrickPane(trick, player, map));
        scorePane.getChildren().add(createScorePane(score, map));
    }

    public Stage createStage() {
        Stage stage = new Stage();
        return stage;
    }

    private GridPane createScorePane(ScoreBean score,
            Map<PlayerId, String> map) {
        GridPane scorePane = new GridPane();

        Text names1 = new Text(map.get(PlayerId.PLAYER_1) + " et "
                + map.get(PlayerId.PLAYER_3) + " : ");
        Text names2 = new Text(map.get(PlayerId.PLAYER_2) + " et "
                + map.get(PlayerId.PLAYER_4) + " : ");

        Text turnPoints1 = new Text(Bindings
                .convert(score.turnPointsProperty(TeamId.TEAM_1)).toString());
        Text turnPoints2 = new Text(Bindings
                .convert(score.turnPointsProperty(TeamId.TEAM_2)).toString());

        Text difference1 = new Text();
        Text difference2 = new Text();

        Text total = new Text("/Total : ");

        Text gamePoints1 = new Text(Bindings
                .convert(score.gamePointsProperty(TeamId.TEAM_1)).toString());
        Text gamePoints2 = new Text(Bindings
                .convert(score.gamePointsProperty(TeamId.TEAM_2)).toString());

        scorePane.addRow(0, names1);
        scorePane.addRow(0, turnPoints1);
        scorePane.addRow(0, difference1);
        scorePane.addRow(0, total);
        scorePane.addRow(0, gamePoints1);

        scorePane.addRow(1, names2);
        scorePane.addRow(1, turnPoints2);
        scorePane.addRow(1, difference2);
        scorePane.addRow(1, total);
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
        ImageView trumpImage = new ImageView(
                "/trump_" + trick.ColorProperty().get().ordinal() + ".png");

        for (int i = 0; i < PlayerId.COUNT; i++) {
            cardImages[i] = new ImageView();
            cardImages[i].imageProperty()
                    .bind(Bindings.valueAt(images, Bindings.valueAt(
                            trick.trick(),
                            PlayerId.values()[(player.ordinal() + i) % 4])));
            cardImages[i].setFitWidth(120);
            cardImages[i].setFitHeight(180);
            names[i] = new Text(
                    map.get(PlayerId.values()[(player.ordinal() + i) % 4]));
            names[i].setStyle("-fx-font: 14 Optima;");
            pairs[i] = new VBox(cardImages[i], names[i]);
        }
        Rectangle halo = new Rectangle(120, 180);
        halo.setStyle(
                "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; -fx-stroke-width: 5; -fx-opacity: 0.5;");
        halo.setEffect(new GaussianBlur(4));
        stack.getChildren().add(halo);

        trickPane.add(pairs[0], 1, 2, 1, 1);
        trickPane.add(pairs[1], 2, 1, 3, 3);
        trickPane.add(pairs[2], 1, 0, 1, 1);
        trickPane.add(pairs[3], 0, 1, 3, 3);
        trickPane.add(trumpImage, 1, 1, 1, 1);
        trickPane.setStyle(
                "-fx-background-color: whitesmoke; -fx-padding: 5px; -fx-border-width: 3px 0px; -fx-border-style: solid; -fx-border-color: gray; -fx-alignment: center;");

        return trickPane;
    }

    private StackPane createWinningPane(ScoreBean score,
            Map<PlayerId, String> map) {
        StackPane winningPane = new StackPane();
        int winnerOrd = score.winningTeamProperty().get().ordinal();
        Text text = new Text(map.get(PlayerId.values()[winnerOrd + 1]) + " et "
                + map.get(PlayerId.values()[winnerOrd + 3]) + " ont gagné avec "
                + score.totalPointsProperty(TeamId.values()[winnerOrd])
                + " contre " + score.totalPointsProperty(
                        TeamId.values()[(winnerOrd + 1) % 2]));
        text.setStyle("-fx-font: 14 Optima;");

        winningPane.getChildren().add(text);
        winningPane.setStyle("-fx-background-color: lightgray;\r\n"
                + "-fx-spacing: 5px;\r\n" + "-fx-padding: 5px;");
        return winningPane;
    }
    private HBox createHandPane(HandBean hand, PlayerId player, ArrayBlockingQueue<Card> cardQueue) {
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


}
