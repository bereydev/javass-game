package ch.epfl.javass;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

public class RemoteMain extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Player player = new GraphicalPlayerAdapter();
        Thread serverThread = new Thread(() -> {
            RemotePlayerServer server = new RemotePlayerServer(player);
            System.out.println("La partie commencera à la connexion du client...");
            server.run();
        });
        serverThread.setDaemon(true);
        serverThread.start();

    }
}
