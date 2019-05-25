/*
 *	Author : Alexandre Santangelo & Jonathan Bereyziat
 *	Date   : Apr 15, 2019	
*/

package ch.epfl.javass.net;

import ch.epfl.javass.jass.Player;

import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

import static java.nio.charset.StandardCharsets.US_ASCII;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public final class RemotePlayerServer {

    private final Player player;
    private final static int COMMAND_INDEX = 0;
    private final static int FIRST_PARAMETER = 1; 
    private final static int SECOND_PARAMETER = 2; 

    /**
     * The constructor of the RemotePlyerServer take an underlying Player as
     * parameter
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
    }

    /**
     * Lauch a "Serveur" which is communicating with the main game and is
     * passing informations to the underlying Player mainly a human player who
     * plays on another machine
     */
    public void run() {

        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();

                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), US_ASCII))) {
            while (!s.isClosed()) {
                String[] message = r.readLine().trim().split(" ");
                JassCommand command = JassCommand.valueOf(message[COMMAND_INDEX]);
                switch (command) {
                case PLRS:
                    PlayerId ownId = PlayerId.values()[Integer
                            .parseInt(message[FIRST_PARAMETER])];
                    String[] names = StringSerializer.split(message[2]);
                    Map<PlayerId, String> map = new HashMap<>();
                    for (int i = 0; i < PlayerId.COUNT; i++) {
                        map.put(PlayerId.values()[i],
                                StringSerializer.deserializeString(names[i]));
                    }

                    player.setPlayers(ownId, map);
                    break;
                case CARD:
                    String[] stateTab = StringSerializer.split(message[FIRST_PARAMETER]);
                    TurnState state = TurnState.ofPackedComponents(
                            
                            StringSerializer.deserializeLong(stateTab[0]), // Score
                            StringSerializer.deserializeLong(stateTab[1]), // CardSet
                                                                           // not
                                                                           // played
                            StringSerializer.deserializeInt(stateTab[2])); // Trick
                    CardSet hand = CardSet.ofPacked(
                            StringSerializer.deserializeLong(message[SECOND_PARAMETER]));

                    Card card = player.cardToPlay(state, hand);

                    // Answering
                    w.write(StringSerializer.serializeInt(card.packed()));
                    w.write("\n");
                    w.flush();
                    break;
                case HAND:
                    CardSet set = CardSet.ofPacked(
                            StringSerializer.deserializeLong(message[FIRST_PARAMETER]));
                    player.updateHand(set);

                    break;
                case SCOR:
                    Score score = Score.ofPacked(
                            StringSerializer.deserializeLong(message[FIRST_PARAMETER]));
                    player.updateScore(score);

                    break;
                case TRCK:
                    Trick trick = Trick.ofPacked(
                            StringSerializer.deserializeInt(message[FIRST_PARAMETER]));
                    player.updateTrick(trick);

                    break;
                case TRMP:
                    player.setTrump(
                            Card.Color.values()[StringSerializer.deserializeInt(message[FIRST_PARAMETER])]);

                    break;
                case WINR:
                    player.setWinningTeam(
                            TeamId.values()[StringSerializer.deserializeInt(message[FIRST_PARAMETER])]);
                    System.out.println("server closed");
                    w.close();
                    r.close();
                    s.close();
                    s0.close();
                    return; 
                    
                case SETRMP: 
                    CardSet myCards = CardSet
                    .ofPacked(StringSerializer.deserializeLong(message[1]));
                    
                    int trump = player.trumpToPlay(myCards).ordinal(); 
                    //Answers the trump to set 
                    w.write(StringSerializer.serializeInt(trump));
                    w.write("\n");
                    w.flush();
                    
                default:
                    System.err.println("Huston we have a problem");
                    System.exit(1);
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

}
