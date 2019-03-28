/*
 *	Author : Alexandre Santangelo & Jonathan Bereyziat
 *
 *	Date   : Mar 24, 2019	
*/

package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
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
        Node root = new Node(state, state.trick().playableCards(hand), ownId,
                hand);
        List<Node> nodeList;  
        for (int i = 0; i < iterations; i++) {
            nodeList = root.addNode();
            Collections.reverse(nodeList); 
            double totalPoints=0; 
            for(Node n : nodeList) {
                totalPoints += n.totalPoints; 
                n.totalPoints = totalPoints/n.nbOfTurns; 
            }
            Collections.reverse(nodeList);
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
        private Node(TurnState currentState, CardSet cardset, PlayerId ownId,
                CardSet hand) {
            cardSetForNextNodes = cardset;
            this.currentState = new TurnState(currentState);
            this.ownId = ownId;
            children = new Node[cardset.size()];
            this.hand = hand;
            this.totalPoints = runRandomGame().gamePoints(ownId.team()); 
        }
        /**
         * @return the score of the randomly simulated Jass game 
         */
        private Score runRandomGame() {
            TurnState stateCopy = new TurnState(currentState);
            CardSet cards = CardSet.ALL_CARDS.intersection(cardSetForNextNodes);
            PlayerId player;
            while (!stateCopy.isTerminal() ) {
                while (stateCopy.trick().size() < 4) {
                    player = stateCopy.nextPlayer();
                    cards = currentPlayableCards(cards, stateCopy, player);
                    Card cardToPlay = cards.get(0);
                    cards.remove(cardToPlay);
                    hand.remove(cardToPlay);
                    stateCopy = stateCopy.withNewCardPlayed(cardToPlay);
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

        private CardSet currentPlayableCards(CardSet cards,
                TurnState currentState, PlayerId player) {
            if (player.equals(ownId)) {
                return currentState.trick().playableCards(hand);
            }
            return currentState.trick().playableCards(cardSetForNextNodes.
                    intersection(hand.complement())); 
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
                        nextCurrentState.unplayedCards(), ownId, hand.remove(cardSetForNextNodes.get(0)));
            }
            return nodes;

        }

    }

}
