/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 24, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.SplittableRandom;

public final class MctsPlayer implements Player {
    
    
    private PlayerId ownId; 
    private long rngSeed; 
    private int iterations; 
    
    
    
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if(iterations<9)
            throw new IllegalArgumentException(); 
        this.iterations = iterations; 
        this.rngSeed = rngSeed; 
        this.ownId = ownId; 
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node first = new Node(state, state.trick().playableCards(hand)); 
        return null;
    }
    
    private List<Node> addNode(){
        return null;
    }
    
    private static class Node{
        private TurnState currentState; 
        private Node[] children; 
        private CardSet cardsetForNextNodes; 
        private int totalPoints; 
        private int nbOfTurns = 0; 
        private Node parent = null;
        
        Node(TurnState currentState,CardSet cardset) {
            cardsetForNextNodes = cardset; 
            this.currentState = new TurnState(currentState); 
            this.totalPoints = totalPoints; 
            this.nbOfTurns = nbOfTurns; 
            children = new Node[cardset.size()];
            
        }
       
        Node(TurnState currentState,CardSet cardset, Node parent) {
            this(currentState, cardset);
            this.parent = parent;
        }
        int finalScore() {
            return totalPoints;
        }
        
        CardSet currentPlayableCards() {
            return null;
        }
        
        void runRandomGame() {
//            SplittableRandom rng = new SplittableRandom(); 
//            while (! currentState.isTerminal()) {
//                currentState.withNewCardPlayed(card)
//              }
//            totalPoints = 
        }
        
        int selectChild() {
            return 0;
        }
        
        
    }
    

}
