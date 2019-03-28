/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 24, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.SplittableRandom;
import java.lang.Math;

public final class MctsPlayer implements Player {

    private PlayerId ownId;
    private long rngSeed;
    private int iterations;

    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if (iterations < 9)
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
   
        
        Node(TurnState currentState, CardSet cardset, PlayerId ownId, CardSet hand) {
            unplayedCards = cardset;
            this.currentState = new TurnState(currentState);
            this.ownId = ownId; 
            children = new Node[cardset.size()];
            this.hand = hand; 

        }

        Node(TurnState currentState, CardSet cardset,PlayerId ownId, Node parent,CardSet hand) {
            unplayedCards = cardset;
            this.currentState = new TurnState(currentState);
            this.ownId = ownId; 
            children = new Node[cardset.size()];
            this.parent = parent;
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
       

        int selectChild(int c) {
            boolean noChild = true; // the array of children is empty
            int nbrChild = 0;
            for (Object ob : children) {
                if (ob != null) {
                    noChild = false;
                    nbrChild++;
                }
            }
            assert (!noChild); // you can't choose the best child if there is
                               // non childNode in the Array

            int indexOfTheBestChild = 0;
            double bestNodeValue = 0;
            for (int i = 0; i < nbrChild; i++) {
                int S = children[i].totalPoints;
                int N = children[i].nbOfTurns;
                int Np = nbOfTurns;
                double nodeValue = S / N + c * Math.sqrt(2 * Math.log(N) / Np);
                if (nodeValue >= bestNodeValue) {
                    bestNodeValue = nodeValue;
                    indexOfTheBestChild = i;
                }
            }
            return indexOfTheBestChild;
        }

        List<Node> addNode() {
            List<Node> nodes = new ArrayList<MctsPlayer.Node>();
            nodes.add(this);

             if (currentState.isTerminal()) {
                 return nodes ;
             }
            boolean isFull = true; // the array of children is full
            int indexToAdd = 0;
            for (int i = 0; i < children.length; i++) {
                if (children[i] == null) {
                    isFull = false;
                    indexToAdd = i;
                    break;
                }
            }
            if (isFull) {
                Node selectedChild = children[selectChild(40)];
                nodes.addAll(selectedChild.addNode());
            } else {
                TurnState nextCurrentState = currentState
                        .withNewCardPlayedAndTrickCollected(
                                unplayedCards.get(0));
                // WARNING if the currentState arrived with a full trick an
                // IllecgalStateException is thrown but in theory it shouldn't
                // happen
                children[indexToAdd] = new Node(nextCurrentState,
                        nextCurrentState.unplayedCards(), ownId, this, hand.remove(unplayedCards.get(0)));
            }
            return nodes;

        }

    }

}
