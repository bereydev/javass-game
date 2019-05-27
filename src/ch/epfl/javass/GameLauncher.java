package ch.epfl.javass;

import java.io.IOException;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameLauncher extends Application {

    private static final String NAME[] = { "Aline", "Bastien", "Colette",
            "David" };
    private static final String DEFAULT_HOST = "localhost";
    private static final int PLAY_TIME = 2; // time expressed in second
    private Random rng = new Random(0);
    private String[][] parameters = new String[4][3];

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        
        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));
        
        Text title = new Text("Configurez votre partie de Jass");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 35));
        grid.add(title, 0, 0, 2, 1);
        
        
        for (PlayerId player : PlayerId.ALL) {
            String playerSet[] = parameters[player.ordinal()];
            GridPane playerGrid = new GridPane();
            Text playerTag = new Text("Joueur " + (player.ordinal()+1));
            playerTag.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            Label playerName = new Label("Nom du Joueur:");
            TextField playerTextField = new TextField();
            playerTextField.setText(NAME[player.ordinal()]);
            Label hostLabel = new Label("Adresse IP :");
            TextField hostTextField = new TextField();
            hostTextField.setText(DEFAULT_HOST);
            Label hardnessLabel = new Label("Difficulté :");
            Button btnEasy = new Button();
            btnEasy.setText("Facile");
            Button btnMedium = new Button();
            btnMedium.setText("Moyenne");
            Button btnHard = new Button();
            btnHard.setText("Difficile");
            Button btnHumanPlayer = new Button();
            Button btnRemotePlayer = new Button();
            Button btnSimulatePlayer = new Button();
            
           
            GridPane hardnessGrid = new GridPane();
            hardnessGrid.setHgap(10);
            hardnessGrid.setVgap(10);
            hardnessGrid.add(hardnessLabel,0,0,3,1);
            hardnessGrid.add(btnEasy, 0, 1);
            hardnessGrid.add(btnMedium, 1, 1);
            hardnessGrid.add(btnHard, 2, 1);
            hardnessGrid.setVisible(false);
            
            GridPane hostGrid = new GridPane();
            hostGrid.setHgap(10);
            hostGrid.setVgap(10);
            hostGrid.add(hostLabel, 0, 0);
            hostGrid.add(hostTextField,1,0);
            hostGrid.setVisible(false);
            
            GridPane typeGrid = new GridPane();
            typeGrid.setHgap(10);
            typeGrid.setVgap(10);
            typeGrid.add(btnHumanPlayer, 0, 0);
            typeGrid.add(btnRemotePlayer, 1,0);
            typeGrid.add(btnSimulatePlayer, 2, 0);
            
            
            playerTextField.textProperty().addListener((o,oV,nV) -> {
                playerSet[1] = nV;
            });
            
            hostTextField.textProperty().addListener((o,oV,nV) -> {
                playerSet[2] = nV;
            });
            btnEasy.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent addHuman) { 
                    btnEasy.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                    btnMedium.setStyle("-fx-background-color:#E0E0E0;");
                    btnHard.setStyle("-fx-background-color:#E0E0E0;");
                    playerSet[2] = "10";
                }
            });
            
           btnMedium.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent addHuman) { 
                   btnMedium.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                   btnEasy.setStyle("-fx-background-color:#E0E0E0;");
                   btnHard.setStyle("-fx-background-color:#E0E0E0;");
                   playerSet[2] = "5000";
               }
           });
           
           btnHard.setOnAction(new EventHandler<ActionEvent>() {
               @Override
               public void handle(ActionEvent addHuman) { 
                   btnHard.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                   btnMedium.setStyle("-fx-background-color:#E0E0E0;");
                   btnEasy.setStyle("-fx-background-color:#E0E0E0;");
                   playerSet[2] = "100000";
               }
           });
           
            
            btnHumanPlayer.setText("Local");
            btnHumanPlayer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent addHuman) { 
                    hostGrid.setVisible(false);
                    hardnessGrid.setVisible(false);
                    System.out.println("Joueur " + (player.ordinal()+1) +" : Humain");
                    btnHumanPlayer.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                    btnSimulatePlayer.setStyle("-fx-background-color:#E0E0E0;");
                    btnRemotePlayer.setStyle("-fx-background-color:#E0E0E0;");
                    playerSet[1] = playerTextField.getText();
                    playerSet[0] = "h";
                }
            });
            
            btnRemotePlayer.setText("Distant"); 
            btnRemotePlayer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent addRemote) { 
                    System.out.println("Joueur " + (player.ordinal()+1) +" : Distant");
                    hostGrid.setVisible(true);
                    hardnessGrid.setVisible(false);
                    btnRemotePlayer.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                    btnSimulatePlayer.setStyle("-fx-background-color:#E0E0E0;");
                    btnHumanPlayer.setStyle("-fx-background-color:#E0E0E0;");
                    playerSet[1] = playerTextField.getText();
                    playerSet[2] = hostTextField.getText();
                    playerSet[0] = "r";
                }
            }); 
            
            btnSimulatePlayer.setText("Simulé");
            btnSimulatePlayer.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent addSimulate) {
                    hostGrid.setVisible(false);
                    hardnessGrid.setVisible(true);
                    System.out.println("Joueur " + (player.ordinal()+1) +" : Simulé");
                    btnSimulatePlayer.setStyle("-fx-background-color: #ff0000; -fx-text-fill: #FFFFFF ");
                    btnHumanPlayer.setStyle("-fx-background-color:#E0E0E0;");
                    btnRemotePlayer.setStyle("-fx-background-color:#E0E0E0;");
                    playerSet[1] = playerTextField.getText();
                    playerSet[0] = "s";
                }
            });
            
            playerGrid.setHgap(10);
            playerGrid.setVgap(10);
            playerGrid.setPadding(new Insets(25, 25, 25, 25));
            playerGrid.setAlignment(Pos.CENTER);
            playerGrid.add(playerTag, 0, 0, 3, 1);
            playerGrid.add(playerName, 0, 1);
            playerGrid.add(playerTextField, 1, 1);
            playerGrid.add(hostGrid, 0, 2, 3,1);
            playerGrid.add(hardnessGrid, 0, 3, 3, 2);
            playerGrid.add(typeGrid, 0, 5, 3, 1);
            
            int column = player.ordinal() % 2;
            int row = player.ordinal() == 0 || player.ordinal() == 1 ? 1 : 2;
            grid.add(playerGrid, column, row);
            
        }
        Button btnStart = new Button();
        btnStart.setText("Lancer le jeu");
        grid.add(btnStart, 0, 3);

        btnStart.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent startTheGame) { 
                for (PlayerId player : PlayerId.ALL) {
                    String type = parameters[player.ordinal()][0];
                    String name = parameters[player.ordinal()][1];
                    String specific = parameters[player.ordinal()][2];
                    System.out.println(specific);
                    switch (type) {
                    case "h":
                        ps.put(player, new GraphicalPlayerAdapter());
                        ns.put(player, name);
                        break;
                    case "s":
                        ps.put(player, new PacedPlayer(
                                new MctsPlayer(player, rng.nextInt(), Integer.parseInt(specific)),
                                PLAY_TIME));
                        ns.put(player, name); 
                        break;
                    case "r":
                        try {
                            ps.put(player, new RemotePlayerClient(specific)); 
                        } catch (IOException e) {
                            System.err.println(
                                    "Erreur : Connexion au serveur impossible ou refusée "
                                            + "veuillez vérifier les paramètre d'hôte passé "
                                            + "en paramètre ou désactiver votre anti-virus");
                        }
                        ns.put(player, name);
                        break;

                    default:
                        break;
                    }
                }
                
                System.out.println(ns);
                System.out.println(ps);
                Thread gameThread = new Thread(() -> {
                    JassGame g = new JassGame(rng.nextInt(), ps, ns);
                    while (!g.isGameOver()) {
                        g.advanceToEndOfNextTrick();
                        try {
                            Thread.sleep(4000);
                        } catch (Exception e) {
                        }
                    }
                });
                gameThread.setDaemon(true);
                gameThread.start();
            }
        });

        Scene scene = new Scene(grid, 1000, 700);

        primaryStage.setTitle("Javass - Créer votre partie !");
        primaryStage.setScene(scene);
        primaryStage.show();

    }

}
