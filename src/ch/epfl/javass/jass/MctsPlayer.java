/*
 *	Author : Alexandre Santangelo & Jonathan Bereyziat
 *
 *	Date   : Mar 24, 2019	
*/

package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.SplittableRandom;
import java.lang.Math;

public final class MctsPlayer implements Player {

    private PlayerId ownId;
    private long rngSeed;
    private int iterations;

    /**
     * @param ownId
     *            the Id of the player that will be a MctsPlayer
     * @param rngSeed
     *            the seed used for the random number Generation
     * @param iterations
     *            the number of iterations must be greater than 9 and less than
     *            the maximal number of nodes
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        if (iterations < 9)
            throw new IllegalArgumentException();
        this.iterations = iterations;
        this.rngSeed = rngSeed;
        this.ownId = ownId;
        System.out.println("I'm in team: " + ownId.team());
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node root = new Node(state, state.unplayedCards(), ownId,
                hand,rngSeed);
        List<Node> nodeList;  
        for (int i = 0; i < iterations; i++) {
            nodeList = root.addNode();
            Collections.reverse(nodeList); 
            double totalPoints=0; 
            for(Node n : nodeList) {
                totalPoints += n.totalPoints; 
                n.totalPoints = totalPoints/n.nbOfTurns; 
            }
        }
        return hand.get(root.selectChild(0));
    }

    /**
     * Node object represent a Node of the Monte Carlo Tree
     *
     */
    private static class Node {
        private TurnState currentState;
        private Node[] children;
        private CardSet cardSetForNextNodes;
        private CardSet hand;
        private double totalPoints;
        private int nbOfTurns = 0;
        private final PlayerId ownId;
        private final SplittableRandom rng; 
        private final long seed; 

        /**
         * @param currentState
         *            the State of the game where the node is created
         * @param cardset
         *            the Set of card that remains to play
         * @param ownId
         *            the id of the proprietary MctsPlayer
         * @param hand
         *            the hand of the MctsPlayer
         */
        private Node(TurnState currentState, CardSet cardSet, PlayerId ownId,
                CardSet hand, long seed) {
            this.hand = CardSet.ALL_CARDS.intersection(hand);
            cardSetForNextNodes = CardSet.ALL_CARDS.intersection(cardSet); 
            for(int i=0; i<this.hand.size(); i++) {
               cardSetForNextNodes = cardSetForNextNodes.remove(this.hand.get(i)); 
            }
            this.currentState = new TurnState(currentState);
            this.ownId = ownId;
            children = new Node[cardSetForNextNodes.size()];
            
            this.seed = seed; 
            this.rng = new SplittableRandom(seed); 
            this.totalPoints = runRandomGame().gamePoints(ownId.team()); 
        }
        /**
         * @return the score of the randomly simulated Jass game 
         */
        private Score runRandomGame() {
            TurnState stateCopy = new TurnState(currentState);
            CardSet cardsNotHand = CardSet.ALL_CARDS.intersection(cardSetForNextNodes); 
            CardSet handCopy = CardSet.ALL_CARDS.intersection(hand); 
            CardSet cards; 
            PlayerId player;
            Card cardToPlay; 
            System.out.println("My hand is "+handCopy + " I'm "+ ownId);
            System.out.println("The cards are "+cardsNotHand);
            while (!stateCopy.isTerminal() ) {
                while (stateCopy.trick().size() < 4) {
                    player = stateCopy.nextPlayer();
                    
                    if(player.equals(ownId)) {
                        cards = stateCopy.trick().playableCards(handCopy);
                    }  
                    else {
                        cards = stateCopy.trick().playableCards(cardsNotHand);
                    }
                    
                    if(cards.size()==0)
                        cards = handCopy.union(cardsNotHand);
                   
                    cardToPlay = cards.get(rng.nextInt(cards.size()));
//                    System.out.println(player + " plays "+ cardToPlay + " pli nb " + 
//                    stateCopy.trick().index() + ". cards remaining " +
//                            (cardsNotHand.size()+handCopy.size()));
                    
                    
                    stateCopy = stateCopy.withNewCardPlayed(cardToPlay);
                    
                    if(player.equals(ownId))
                        handCopy = handCopy.remove(cardToPlay); 
                    else 
                        cardsNotHand = cardsNotHand.remove(cardToPlay); 
                        
                }
                stateCopy = stateCopy.withTrickCollected();
            }
            return stateCopy.score();
        }

        /**
         * @return the final Score of the randomly generated game of Jass in the
         *         node
         */
        private Score finalScore() {
            return null;
        }

        /**
         * @param c
         *            the constant (40) of exploration or the MCTS if c = 0 we
         *            obtain the next card to play for the MctsPlayer
         * @return the Best child of a node regarding the formulae of the MCTS
         */
        private int selectChild(int c) {
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
                double S = children[i].totalPoints;
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

        /**
         * @return A list of nodes from the one added by the function to the
         *         root (the one to which the function is applied)
         */
        private List<Node> addNode() {
            List<Node> nodes = new ArrayList<MctsPlayer.Node>();
            nodes.add(this);

            if (currentState.isTerminal()) {
                return nodes;
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
                                cardSetForNextNodes.get(0));
                // WARNING if the currentState arrived with a full trick an
                // IllecgalStateException is thrown but in theory it shouldn't
                // happen
                children[indexToAdd] = new Node(nextCurrentState,
                        cardSetForNextNodes.remove(cardSetForNextNodes.get(0)), ownId,
                        hand.remove(cardSetForNextNodes.get(0)),this.seed);
            }
            return nodes;

        }

    }

}
