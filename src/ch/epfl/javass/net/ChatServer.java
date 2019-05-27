package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;

import ch.epfl.javass.gui.MessageId;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;

public final class ChatServer {
    
    private final Player player;
    private static final int COMMAND = 0;
    private static final int PLAYER = 1;
    private static final int MESSAGE = 2;
    
    public ChatServer(Player player) {
        this.player = player;
    }
    
    public void run() {
        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();

                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), US_ASCII))) {
            while (!s.isClosed()) {
                String[] message = r.readLine().trim().split(" ");
                if (message[COMMAND] == JassCommand.MSG.name()) {
                    PlayerId playerToUpdate = PlayerId.ALL.get(StringSerializer.deserializeInt(message[PLAYER]));
                    MessageId receivedMessage = MessageId.ALL.get(StringSerializer.deserializeInt(message[MESSAGE]));
                    player.catchMessage(playerToUpdate,receivedMessage);
                }  
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
}
