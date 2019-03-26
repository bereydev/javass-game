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
        Node first = new Node(state, hand, state.score().totalPoints(ownId.team()),1); 
        return null;
    }
    
    private static class Node{
        private static final int MAX_NB_OF_NODES =9; 
        private TurnState currentState; 
        private Node[] children = new Node[MAX_NB_OF_NODES]; 
        private CardSet cardsetForNextNodes; 
        private int totalPoints; 
        private int nbOfTurns; 
        private TeamId ownerTeam; 
        
        Node(TurnState currentState,CardSet cardset, int totalPoints, int nbOfTurns) {
            cardsetForNextNodes = cardset; 
            this.currentState = new TurnState(currentState); 
            this.totalPoints = totalPoints; 
            this.nbOfTurns = nbOfTurns; 
        }
    }
    

}
