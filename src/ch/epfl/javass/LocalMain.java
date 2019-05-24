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
    private static final int NAME_ARGS_LENGTH = 2;
    private static final int NAME_INDEX = 1;
    private static final int SPEC_ARGS_LENGTH = 3;
    private static final int SPEC_INDEX = 2;
    private static final int TYPE_INDEX = 0;
    private static final int MAX_GLOBAL_ARGS = 5;
    private static final int MIN_GLOBAL_ARGS = 4;
    private static final long THREAD_SLEEP_TIME = 1000;
    private Random rng = new Random(0);
    private final Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);
    private final Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
    private static final String EXPLICATIONS = "Utilisation: java ch.epfl.javass.LocalMain <j1>…<j4> [<graine>]\n"
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
        if (parameters.size() > MAX_GLOBAL_ARGS || parameters.size() < MIN_GLOBAL_ARGS) {
            System.err.println(
                    "Erreur : Nombre d'arguments invalide\n" + EXPLICATIONS);
            System.exit(1);
        }
        if (parameters.size() == MAX_GLOBAL_ARGS)
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
                                + EXPLICATIONS);
                System.exit(1);
            }
            switch (sets[TYPE_INDEX]) {
            //TODO enum 
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
                    Thread.sleep(THREAD_SLEEP_TIME);
                    // 2s to wait before collecting the trick
                } catch (Exception e) {
                }
            }
        });
        gameThread.setDaemon(true);
        gameThread.start();

    }

    private void createHumanPlayer(PlayerId player, String[] sets) {
        if (sets.length > NAME_ARGS_LENGTH) {
            System.err.println(
                    "Erreur : Les spécification du joueur humain comportent trop de composantes\n"
                    + "le joueur humain doit être spécifié ainsi\n"
                    + "h[:<nom>] où <nom> correspond au nom du joeur\n"
                    + "les parametres entre [] sont optionnels");
            System.exit(1);
        }
        ps.put(player, new GraphicalPlayerAdapter());
        if (sets.length == NAME_ARGS_LENGTH)
            ns.put(player, sets[NAME_INDEX]);
        else
            ns.put(player, NAME[player.ordinal()]);
    }

    private void createSimulatePlayer(PlayerId player, String[] sets) {
        int itterations = ITTERATIONS;
        if (sets.length == SPEC_ARGS_LENGTH)
            try {
                itterations = Integer.parseInt(sets[SPEC_INDEX]);

            } catch (NumberFormatException e) {
                System.err.println(
                        "Erreur : Le nombre d'ittération doit être un nombre entier valide");
                System.exit(1);
            }
        if (sets.length >= NAME_ARGS_LENGTH && !sets[NAME_INDEX].trim().isEmpty()) {
            ns.put(player, sets[NAME_INDEX]);
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
        if (sets.length == SPEC_ARGS_LENGTH)
            host = sets[SPEC_INDEX];
        if (sets.length >= NAME_ARGS_LENGTH && !sets[NAME_INDEX].trim().isEmpty())
            ns.put(player, sets[NAME_INDEX]);
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
