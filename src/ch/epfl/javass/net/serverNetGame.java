package ch.epfl.javass.net;
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.RandomPlayer;
import ch.epfl.javass.net.RemotePlayerClient;
import ch.epfl.javass.net.RemotePlayerServer;


public class serverNetGame {
    public static void main(String[] args) throws IOException {
        
        Player player = new RandomPlayer(2019);
        RemotePlayerServer server =  new RemotePlayerServer(player);
        server.run();
    }
}
