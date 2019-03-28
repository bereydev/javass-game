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
import java.util.Random;
import java.util.SplittableRandom;

public final class MctsPlayer implements Player {
    
    
    private final PlayerId ownId; 
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
        Node first = new Node(state, state.trick().playableCards(hand), ownId, hand); 
        return null;
    }
    
    private List<Node> addNode(){
        return null;
    }
    

    
    private static class Node{
        private TurnState currentState; 
        private Node[] children; 
        private CardSet unplayedCards; 
        private CardSet hand; 
        private int totalPoints; 
        private int nbOfTurns=0; 
        private Node parent=null;
        private final PlayerId ownId; 
        
        
        
        Node(TurnState previousCurrentState,CardSet unplayedCards, PlayerId ownId, CardSet hand) {
            this.unplayedCards = unplayedCards; 
            this.currentState = new TurnState(previousCurrentState); 
            children = new Node[unplayedCards.size()];
            this.ownId = ownId; 
            this.hand = hand; 
            
        }
       
        Node(TurnState previousCurrentState,CardSet unplayedCards, Node parent,CardSet hand) {
            this.unplayedCards = unplayedCards; 
            this.parent = parent;
            this.ownId = parent.ownId; 
            this.currentState = new TurnState(previousCurrentState); 
            children = new Node[unplayedCards.size()];
            this.hand = hand; 
        }
        void runRandomGame() {
            TurnState stateCopy = new TurnState(currentState); 
            CardSet cards = CardSet.ALL_CARDS.intersection(unplayedCards); 
            SplittableRandom rng = new SplittableRandom(); 
            PlayerId player; 
            while(!stateCopy.isTerminal()) {
                while(stateCopy.trick().size() <4) {
                    player = stateCopy.nextPlayer(); 
                    cards  = currentPlayableCards(cards,stateCopy, player); 
                    Card cardToPlay = cards.get(rng.nextInt(cards.size()));
                    
                    cards.remove(cardToPlay); 
                    hand.remove(cardToPlay); 
                    stateCopy.trick().withAddedCard(cardToPlay); 
                }
                stateCopy = stateCopy.withTrickCollected(); 
            }
         }
        
        int selectChild() {
            return 0;
        }
        
        Score finalScore() {
            return null; 
        }
        CardSet currentPlayableCards(CardSet cards,TurnState currentState, PlayerId player) {
            if(player.equals(ownId)) 
                return  currentState.trick().playableCards(hand); 
            
            return currentState.trick().playableCards(cards).intersection(hand.complement()); 
        }
       
    }
    

}
