package ch.epfl.javass;

import java.io.IOException;
import java.util.EnumMap;
import java.util.List;
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
import javafx.stage.Stage;

public class LocalMain extends Application {

    private static final String NAME[] = { "Aline", "Bastien", "Colette",
            "David" };
    private static final int ITTERATIONS = 10_000;
    private static final String DEFAULT_HOST = "localhost";
    private static final int PLAY_TIME = 2; // time expressed in second
    private Random rng = new Random(0);
    // TODO final ou pas
    private Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
    private Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
    private static final String explications = "Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]\n"
            + "où :\n" + "<jn> spécifie le joueur n, ainsi:\n"
            + "   h:<nom>  un joueur humain nommé <nom>\n"
            + "   r:<nom>:<IP>  un joueur à distance nommé <nom> et"
            + " son adresse IP <IP>\n"
            + "   s:<nom>:<iterations>  un joueur simulé nommé <nom> et"
            + " son nombre d'iterations <iterations>\n"
            + "Vous pouvez ommettre le nom et/ou le nombre de simulations";

    /**
     * Program that is launched on the main device (where the JassGame is really
     * played)
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage arg0) throws Exception {
        List<String> parameters = getParameters().getRaw();
        if (parameters.size() > 5 || parameters.size() < 4) {
            System.err.println(
                    "Erreur : Nombre d'arguments invalide\n" + explications);
            System.exit(1);
        }
        if (parameters.size() == 5)
            try {
                rng = new Random(Long.parseLong(parameters.get(4)));
            } catch (NumberFormatException e) {
                System.err.println(
                        "Erreur : la graine passée en argument doit être de type long");
                System.exit(1);
            }
        for (PlayerId player : PlayerId.ALL) {
            String sets[] = parameters.get(player.ordinal()).split(":");
            if (sets.length > 3) {
                System.err.println(
                        "Erreur : Les spécification d'un joueur comportent trop de composantes\n"
                                + explications);
                System.exit(1);
            }
            switch (sets[0]) {

            case "h":
                createHumanPlayer(player, sets);
                break;
            case "s":
                createSimulatePlayer(player, sets);
                break;
            case "r":
                createRemotePlayer(player, sets);
                break;
            default:
                System.err.println(
                        "Erreur : l'argument pour le type de joueur est invalide\n"
                        + "Vous pouvez ajouter un joueur ainsi:\n"
                        + "h pour un joueur humain\n"
                        + "s pour un joueur simulé\n+"
                        + "r pour un joueur distant");
                System.exit(1);
            }
        }

        Thread gameThread = new Thread(() -> {
            JassGame g = new JassGame(rng.nextInt(), ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();
                try {
                    Thread.sleep(2000);
                    // 2s to wait before collecting the trick
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();

    }

    // TODO des if dans les cas ça passe ?
    private void createHumanPlayer(PlayerId player, String[] sets) {
        if (sets.length > 2) {
            System.err.println(
                    "Erreur : Les spécification du joueur humain comportent trop de composantes\n"
                    + "le joueur humain doit être spécifié ainsi\n"
                    + "h[:<nom>] où <nom> correspond au nom du joeur\n"
                    + "les parametres entre [] sont optionnels");
            System.exit(1);
        }
        ps.put(player, new GraphicalPlayerAdapter());
        if (sets.length == 2)
            ns.put(player, sets[1]);
        else
            ns.put(player, NAME[player.ordinal()]);
    }

    private void createSimulatePlayer(PlayerId player, String[] sets) {
        int itterations = ITTERATIONS;
        if (sets.length == 3)
            try {
                itterations = Integer.parseInt(sets[2]);

            } catch (NumberFormatException e) {
                System.err.println(
                        "Erreur : Le nombre d'ittération doit être un nombre entier valide");
                System.exit(1);
            }
        if (sets.length >= 2 && !sets[1].trim().isEmpty()) {
            ns.put(player, sets[1]);
        } else {
            ns.put(player, NAME[player.ordinal()]);
        }
        try {
            ps.put(player,
                    new PacedPlayer(
                            new MctsPlayer(player, rng.nextInt(), itterations),
                            PLAY_TIME));
        } catch (IllegalArgumentException e) {
            System.err.println(
                    "Erreur : Le nombre d'ittération doit être supérieur à 9");
            System.exit(1);
        }

    }

    private void createRemotePlayer(PlayerId player, String[] sets) {
        String host = DEFAULT_HOST;
        if (sets.length == 3)
            host = sets[2];
        if (sets.length >= 2 && !sets[1].trim().isEmpty())
            ns.put(player, sets[1]);
        else
            ns.put(player, NAME[player.ordinal()]);
        try {
            ps.put(player, new RemotePlayerClient(host));
        } catch (IOException e) {
            System.err.println(
                    "Erreur : Connexion au serveur impossible ou refusée "
                            + "veuillez vérifier les paramètre d'hôte passé "
                            + "en paramètre ou désactiver votre anti-virus");
        }
    }

}
