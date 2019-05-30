/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.net;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;
import ch.epfl.javass.jass.Card.Color;

/**
 * @author astra
 *
 */
import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;

public final class RemotePlayerClient implements Player, AutoCloseable {
    private static final String SPACE = " ";
    private static final int PORT = 5108;
    private final BufferedReader r;
    private final BufferedWriter w;
    private final Socket s;

    /**
     * Constructor of the RemotePlayerClient, handle the opening of the
     * different IOStream and the opening of the Socket
     * 
     * @param hostName
     *            - IP address of the distant player if you want to play local
     *            its "localhost"
     * @throws IOException
     */
    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, PORT);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), US_ASCII));
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        String players[] = new String[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; i++) {
            players[i] = StringSerializer
                    .serializeString(playerNames.get(PlayerId.values()[i]));
        }
        String serializedPlayers = StringSerializer.combine(players);
        IOWriteAndCheck(JassCommand.PLRS.name(),
                StringSerializer.serializeInt(ownId.ordinal()),
                serializedPlayers);
    }

    @Override
    public void updateHand(CardSet newHand) {
        IOWriteAndCheck(JassCommand.HAND.name(),
                StringSerializer.serializeLong(newHand.packed()));
    }

    @Override
    public void setTrump(Color trump) {
        IOWriteAndCheck(JassCommand.TRMP.name(),
                StringSerializer.serializeInt(trump.ordinal()));
    }

    @Override
    public void updateTrick(Trick newTrick) {
       IOWriteAndCheck(JassCommand.TRCK.name(),
                StringSerializer.serializeInt(newTrick.packed()));
    }

    @Override
    public void updateScore(Score score) {
        IOWriteAndCheck(JassCommand.SCOR.name(),
                StringSerializer.serializeLong(score.packed()));
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        IOWriteAndCheck(JassCommand.WINR.name(),
                StringSerializer.serializeInt(winningTeam.ordinal()));
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card card = null;
        String score = StringSerializer.serializeLong(state.packedScore());
        String unplayedCards = StringSerializer
                .serializeLong(state.packedUnplayedCards());
        String trick = StringSerializer.serializeInt(state.packedTrick());
        IOWriteAndCheck(JassCommand.CARD.name(),
                StringSerializer.combine(score, unplayedCards, trick),
                StringSerializer.serializeLong(hand.packed()));
        try {
            // wait for the response
            int pkCard = StringSerializer.deserializeInt(r.readLine());
            card = Card.ofPacked(pkCard);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return card;
    }
    

    /**
     * write on the OutputStream the passed strings and check the IOExceptions
     */
    private void IOWriteAndCheck(String... string) {
        try {
            for (String s : string) {
                w.write(s + SPACE);
            }
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

    /* (non-Javadoc)
     * @see ch.epfl.javass.jass.Player#trumpToPlay(ch.epfl.javass.jass.CardSet)
     */
    @Override
    public Color trumpToPlay(CardSet hand) {
        IOWriteAndCheck(JassCommand.SETRMP.name(),StringSerializer.serializeLong(hand.packed()));
        Color trump = null; 
        try {
            //wait for the response
            int ord = StringSerializer.deserializeInt(r.readLine());
            trump = Color.values()[ord]; 
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return trump;
    }

}
