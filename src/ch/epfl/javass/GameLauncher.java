package ch.epfl.javass;

import java.io.IOException;
import java.net.InetAddress;
import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.gui.MessageBean;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class GameLauncher extends Application {

    private static final String NAME[] = { "Aline", "Bastien", "Colette",
            "David" };
    private static final int SIMPLE_ITTERATIONS = 9;
    private static final int MEDIUM_ITTERATIONS = 5_000;
    private static final int HARD_ITTERATIONS = 100_000;
    private static final String DEFAULT_HOST = "localhost";
    Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
    Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, StringProperty> typeOfPlayers = new EnumMap<>(
            PlayerId.class);
    private final Map<PlayerId, StringProperty> nameOfPlayers = new EnumMap<>(
            PlayerId.class);
    private final Map<PlayerId, StringProperty> hostOfPlayers = new EnumMap<>(
            PlayerId.class);
    private final Map<PlayerId, IntegerProperty> itterationOfPlayers = new EnumMap<>(
            PlayerId.class);
    private static final int PLAY_TIME = 2; // time expressed in second
    private static final int FINAL_TRICK_TIME = 2000; //time in millisecond
    private Random rng = new Random(0);
    private boolean alreadyConnectRemote = false;

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        GridPane grid = new GridPane();
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(25, 25, 25, 25));

        Text title = new Text("Configurez votre partie de Jass");
        title.setFont(Font.font("Tahoma", FontWeight.NORMAL, 35));
        grid.add(title, 0, 0, 2, 1);

        for (PlayerId player : PlayerId.ALL) {
            GridPane playerGrid = new GridPane();
            Label playerTag = new Label("Joueur " + (player.ordinal() + 1));
            playerTag.setFont(Font.font("Tahoma", FontWeight.NORMAL, 20));
            Label playerName = new Label("Nom du Joueur:");
            TextField playerTextField = new TextField();
            playerTextField.setText(NAME[player.ordinal()]);
            Label hostLabel = new Label("Adresse IP :");
            TextField hostTextField = new TextField();
            hostTextField.setText(DEFAULT_HOST);
            Label hardnessLabel = new Label("Difficulté :");

            RadioButton btnHuman = new RadioButton("Local");
            RadioButton btnRemote = new RadioButton("Distant");
            RadioButton btnSimulate = new RadioButton("Simulé");

            ToggleGroup typeGroup = new ToggleGroup();

            btnHuman.setToggleGroup(typeGroup);
            btnRemote.setToggleGroup(typeGroup);
            btnSimulate.setToggleGroup(typeGroup);

            if (player == PlayerId.PLAYER_1) {
                typeGroup.selectToggle(btnHuman);
                typeGroup.getToggles().forEach(toggle -> {
                    Node node = (Node) toggle;
                    node.setDisable(true);
                    playerGrid.add(
                            new Text("Joueur hébergeant la partie de Jass"), 0,
                            3, 2, 1);
                });
            } else
                typeGroup.selectToggle(btnSimulate);

            HBox typeBox = new HBox(btnHuman, btnRemote, btnSimulate);

            ObservableList<String> hardnessLevels = FXCollections
                    .observableArrayList("Simple", "Moyenne", "Difficile");
            ComboBox<String> hardnessComboBox = new ComboBox<>(hardnessLevels);
            hardnessComboBox.setValue("Simple");

            HBox hardnessBox = new HBox();
            hardnessBox.getChildren().addAll(hardnessLabel, hardnessComboBox);
            HBox hostBox = new HBox();
            hostBox.getChildren().addAll(hostLabel, hostTextField);

            BooleanProperty isSimulate = new SimpleBooleanProperty();
            isSimulate.bind(Bindings.createBooleanBinding(
                    () -> typeGroup.getSelectedToggle().equals(btnSimulate),
                    typeGroup.selectedToggleProperty()));

            BooleanProperty isRemote = new SimpleBooleanProperty();
            isRemote.bind(Bindings.createBooleanBinding(
                    () -> typeGroup.getSelectedToggle().equals(btnRemote),
                    typeGroup.selectedToggleProperty()));

            hardnessBox.visibleProperty().bind(
                    Bindings.when(isSimulate).then(true).otherwise(false));

            hostBox.visibleProperty()
                    .bind(Bindings.when(isRemote).then(true).otherwise(false));

            typeOfPlayers.put(player, new SimpleStringProperty());
            typeOfPlayers.get(player).bind(Bindings.createStringBinding(() -> {
                if (typeGroup.getSelectedToggle().equals(btnRemote))
                    return "r";
                else if (typeGroup.getSelectedToggle().equals(btnSimulate))
                    return "s";
                else
                    return "h";
            }, typeGroup.selectedToggleProperty()));

            nameOfPlayers.put(player, new SimpleStringProperty());
            nameOfPlayers.get(player).bind(playerTextField.textProperty());

            hostOfPlayers.put(player, new SimpleStringProperty());
            hostOfPlayers.get(player).bind(hostTextField.textProperty());

            itterationOfPlayers.put(player, new SimpleIntegerProperty());
            itterationOfPlayers.get(player)
                    .bind(Bindings.createIntegerBinding(() -> {
                        if (hardnessComboBox.getValue() == "Simple")
                            return SIMPLE_ITTERATIONS;
                        else if (hardnessComboBox.getValue() == "Moyenne")
                            return MEDIUM_ITTERATIONS;
                        else
                            return HARD_ITTERATIONS;
                    }, hardnessComboBox.selectionModelProperty()));

            playerGrid.setHgap(10);
            playerGrid.setVgap(10);
            playerGrid.setPadding(new Insets(25, 25, 25, 25));
            playerGrid.setAlignment(Pos.CENTER);
            playerGrid.add(playerTag, 0, 0, 2, 1);
            playerGrid.add(playerName, 0, 1);
            playerGrid.add(playerTextField, 1, 1);
            playerGrid.add(typeBox, 0, 2, 2, 1);
            playerGrid.add(hostBox, 0, 3, 2, 1);
            playerGrid.add(hardnessBox, 0, 3, 2, 1);

            int column = player.ordinal() % 2;
            int row = player.ordinal() == 0 || player.ordinal() == 1 ? 1 : 2;
            grid.add(playerGrid, column, row);

        }
        Scene scene = new Scene(grid, 900, 600);
        primaryStage.setTitle("Javass - Créer votre partie !");
        primaryStage.setScene(scene);
        primaryStage.show();
        MessageBean messageBean = new MessageBean();

        Button btnConnect = new Button("Se connecter ");
        grid.add(btnConnect, 1, 3);
        btnConnect.setOnMouseClicked(e -> {
            if (alreadyConnectRemote) {
                Alert connectError = new Alert(AlertType.WARNING);
                connectError.setTitle("Attention !");
                connectError.setHeaderText("Serveur déjà lancé");
                connectError.setContentText("Il semblerai que le joueur distant soit déjà lancé veuillez attendre que la partie se lance \n"+
                            "Si le problème persiste redémarer le jeu ");
                connectError.show();
                return;
            }
            Alert ipInfo = new Alert(AlertType.INFORMATION);
            ipInfo.setTitle("Information de connexion");
            ipInfo.setContentText(
                    "La partie commencera à la connexion du client. "
                            + "\nVeuillez communiquer votre adresse IP à l'hôte de la partie : \n");
            ButtonType btnDismiss = new ButtonType("C'est fait merci !");
            ipInfo.getButtonTypes().setAll(btnDismiss);
            ipInfo.show();
            String ipAdress;
            try {
                ipAdress = InetAddress.getLocalHost().getHostAddress();
            } catch (Exception e2) {
                ipAdress = "Erreur pas d'accès à l'IP";
            }
            ipInfo.setHeaderText("Adresse IP : " + ipAdress);
            launchRemote(messageBean);

        });
        Button btnStart = new Button("Lancer le jeu");
        grid.add(btnStart, 0, 3);
        btnStart.setOnMouseClicked(e -> launchGame(messageBean));
    }
    
    private String checkedName(String name, PlayerId player) {
        return !name.trim().isEmpty() ? name : NAME[player.ordinal()];
    }

    private void launchGame(MessageBean messageBean) {
        for (PlayerId player : PlayerId.ALL) {
            String type = typeOfPlayers.get(player).getValue();
            String name = nameOfPlayers.get(player).getValue();
            String host = hostOfPlayers.get(player).getValue();
            int itterations = itterationOfPlayers.get(player).getValue();
            switch (type) {
            case "h":
                ps.put(player, new GraphicalPlayerAdapter(messageBean));
                ns.put(player, checkedName(name, player));
                break;
            case "s":
                ps.put(player, new PacedPlayer(
                        new MctsPlayer(player, rng.nextInt(), itterations),
                        PLAY_TIME));
                ns.put(player, checkedName(name, player));
                break;
            case "r":
                try {
                    ps.put(player, new RemotePlayerClient(host));

                } catch (IOException e) {
                    Alert connectError = new Alert(AlertType.WARNING);
                    connectError.setTitle("Attention !");
                    connectError.setHeaderText("Erreur de Connexion");
                    connectError.setContentText(
                            "Connexion au serveur impossible ou refusée "
                                    + "veuillez vérifier les paramètre d'hôte passé "
                                    + "en paramètre ou désactiver votre anti-virus.\n"
                                    + "Vérifier d'avoir correctement connecté le joueur distant.");
                    connectError.show();
                    return;
                }
                ns.put(player, checkedName(name, player));
                break;

            default:
                break;
            }
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(rng.nextInt(), ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(FINAL_TRICK_TIME);
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();
    }

    private void launchRemote(MessageBean messageBean) {
        Player player = new GraphicalPlayerAdapter(messageBean);
        Thread serverThread = new Thread(
                () -> new RemotePlayerServer(player).run());
        alreadyConnectRemote = true;
        serverThread.setDaemon(true);
        serverThread.start();
    }

}