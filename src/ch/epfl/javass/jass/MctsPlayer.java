/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 24, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;


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
    
    private static class Node{
      
        private TurnState currentState; 
        private Node[] children; 
        private CardSet cardsetForNextNodes; 
        private int totalPoints; 
        private int nbOfTurns=0; 
        private Node parent=null; 
        
        
        
        int finalScore() {
            return 0; 
        }
        CardSet currentPlayableCards() {
            return CardSet.EMPTY; 
        }
        
        
    }
    

}
