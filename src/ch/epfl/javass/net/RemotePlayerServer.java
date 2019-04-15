/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

package ch.epfl.javass.net;

import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.PackedCard;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public final class RemotePlayerServer {

    private Player player;

    public RemotePlayerServer(Player player) {
        this.player = player;
    }

    public void run() {

        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();

                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), US_ASCII));
                BufferedWriter w = new BufferedWriter(new OutputStreamWriter(
                        s.getOutputStream(), US_ASCII))) {
            //TODO : Figure what to do here 
            while(true) {
                String[] message = r.readLine().trim().split("\\s+");
                JassCommand command = JassCommand.valueOf(message[0]);
          
                switch (command) {
                case PLRS:
                    PlayerId ownId = PlayerId.values()[Integer
                            .parseInt(message[1])];
                    String[] names = StringSerializer.split(message[2]);
                    Map<PlayerId, String> map = new HashMap<>();
        
                    for (int i = 0; i < PlayerId.COUNT; i++)
                        map.put(PlayerId.values()[i],
                                StringSerializer.deserializeString(names[i]));
        
                    player.setPlayers(ownId, map);
                    break;
                case CARD:
                    String[] stateTab = StringSerializer.split(message[1]);
                    TurnState state = TurnState.ofPackedComponents(
                            StringSerializer.deserializeLong(stateTab[0]),
                            StringSerializer.deserializeLong(stateTab[1]),
                            StringSerializer.deserializeInt(stateTab[2]));
                    CardSet hand = CardSet.ofPacked(StringSerializer.deserializeLong(message[2])); 
                    
                    Card card = player.cardToPlay(state, hand); 
                    
                    //Answering 
                    w.write(StringSerializer.serializeInt(card.hashCode()));
                    w.write("\n");
                    break;
                case HAND:
                    CardSet set = CardSet
                            .ofPacked(StringSerializer.deserializeLong(message[1]));
                    player.updateHand(set);
        
                    break;
                case SCOR:
                    Score score = Score
                            .ofPacked(StringSerializer.deserializeLong(message[1]));
                    player.updateScore(score);
        
                    break;
                case TRCK:
                    Trick trick = Trick
                            .ofPacked(StringSerializer.deserializeInt(message[1]));
                    player.updateTrick(trick);
        
                    break;
                case TRMP:
                    player.setTrump(Card.Color.values()[Integer.parseInt(message[1])]);
        
                    break;
                case WINR:
                    player.setWinningTeam(TeamId.values()[Integer.parseInt(message[1])]);
        
                    break;
                default:
                    System.out.println("Huston we have a problem");
                    break;
                }
            }
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        // TODO waits for the clients message
        // calls the correspondent function from the local player,
        // If cardToPlay, sends the value to the client
    }

}
