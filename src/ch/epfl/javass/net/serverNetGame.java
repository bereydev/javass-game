package ch.epfl.javass.net;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.RandomPlayer;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;


public class serverNetGame {
    public static void main(String[] args) throws IOException {
        
        Player player = new MctsPlayer(PlayerId.PLAYER_2,2019,100);
        RemotePlayerServer server =  new RemotePlayerServer(player);
        System.out.println("Server is up and running!");
        server.run();
    }
}
