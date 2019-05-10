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
    private final String SPACE = " ";
    private final BufferedReader r;
    private final BufferedWriter w;
    private final Socket s;

    public RemotePlayerClient(String hostName) throws IOException {
        s = new Socket(hostName, 5108);
        r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), US_ASCII));
        w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), US_ASCII));

    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        try {
            w.write(JassCommand.PLRS.name());
            String players[] = new String[4];
            for (int i = 0; i < players.length; i++) {
                players[i] = StringSerializer.serializeString(playerNames.get(PlayerId.values()[i]));
            }
            System.out.println(playerNames);
            String serializedPlayers = StringSerializer.combine(players);
            w.write(SPACE + StringSerializer.serializeInt(ownId.ordinal()));
            w.write(SPACE + serializedPlayers);
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateHand(CardSet newHand) {
        try {
        w.write(JassCommand.HAND.name());
            w.write(SPACE + StringSerializer.serializeLong(newHand.packed()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setTrump(Color trump) {
        try {
            w.write(JassCommand.TRMP.name());
            w.write(SPACE + StringSerializer.serializeInt(trump.ordinal()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateTrick(Trick newTrick) {
        try {
            w.write(JassCommand.TRCK.name());
            w.write(SPACE + StringSerializer.serializeInt(newTrick.packed()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void updateScore(Score score) {
        try {
            w.write(JassCommand.SCOR.name());
            w.write(SPACE + StringSerializer.serializeLong(score.packed()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        try {
            w.write(JassCommand.WINR.name());
            w.write(SPACE + StringSerializer.serializeInt(winningTeam.ordinal()));
            w.write("\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }
    
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Card card = null;
        try {
            String score = StringSerializer.serializeLong(state.packedScore());
            String unplayedCards = StringSerializer.serializeLong(state.packedUnplayedCards());
            String trick = StringSerializer.serializeInt(state.packedTrick());
            w.write(JassCommand.CARD.name());
            w.write(SPACE + StringSerializer.combine(score,unplayedCards,trick));
            w.write(SPACE + StringSerializer.serializeLong(hand.packed()));
            w.write("\n");
            w.flush();
            //wait for the response
            int pkCard = StringSerializer.deserializeInt(r.readLine());
            card = Card.ofPacked(pkCard);
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return card;
    }
    private void IOWriteAndCheck(String...string) {
        //for optimisation
        try {
            for (String s : string) {
                w.write(s + SPACE);
            }
            w.write("\n");
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
