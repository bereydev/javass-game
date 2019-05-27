package ch.epfl.javass.net;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;

import ch.epfl.javass.gui.MessageId;
import ch.epfl.javass.jass.PlayerId;

public final class ChatClient implements AutoCloseable{
    private static final int PORT = 5108;
    private final BufferedReader r;
    private final BufferedWriter w;
    private final Socket s;
    
    public ChatClient() throws IOException {
        s = new Socket("localhost", PORT);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), US_ASCII));
    }
    
    public void sendMessage(PlayerId player,MessageId message) {
        try {
            w.write(JassCommand.MSG.name() + " ");
            w.write(StringSerializer.serializeInt(player.ordinal()));
            w.write(StringSerializer.serializeInt(message.ordinal()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        
    }
    
    @Override
    public void close() throws Exception {
        w.flush();
        w.close();
        r.close();
        s.close();
    }
    
}   
