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
        // TODO jeje this is not easy 
        return null;
    }
    
    private static class Node{
        private static final int MAX_NB_OF_NODES =9; 
        private TurnState currentState; 
        private Node[] children = new Node[MAX_NB_OF_NODES]; 
        private CardSet cardset; 
        private int totalPoints; 
        private int nbOfTurns; 
    }
    

}
