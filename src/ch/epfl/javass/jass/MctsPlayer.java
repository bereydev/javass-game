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
        Node first = new Node(state, state.trick().playableCards(hand));
        return null;
    }

    private static class Node {
        private TurnState currentState;
        private Node[] children;
        private CardSet cardsetForNextNodes;
        private int totalPoints;
        private int nbOfTurns = 0;
        private Node parent = null;

        Node(TurnState currentState, CardSet cardset) {
            cardsetForNextNodes = cardset;
            this.currentState = new TurnState(currentState);
            this.totalPoints = totalPoints;
            this.nbOfTurns = nbOfTurns;
            children = new Node[cardset.size()];

        }

        Node(TurnState currentState, CardSet cardset, Node parent) {
            this(currentState, cardset);
            this.parent = parent;
        }

        int finalScore() {
            return totalPoints;
        }

        CardSet currentPlayableCards() {
            return cardsetForNextNodes;
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
                                cardsetForNextNodes.get(0));
                // WARNING if the currentState arrived with a full trick an
                // IllecgalStateException is thrown but in theory it shouldn't
                // happen
                children[indexToAdd] = new Node(nextCurrentState,
                        nextCurrentState.unplayedCards());
            }
            return nodes;

        }

    }

}
