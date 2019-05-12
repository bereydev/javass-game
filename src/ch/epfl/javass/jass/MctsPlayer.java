package ch.epfl.javass.jass;

import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
import ch.epfl.javass.Preconditions;

/**
 * Class representing a simulated player using the MCTS algorithm
 *
 * @author Jonathan Bereyziat
 * @author Alexandre Santangelo
 */
public final class MctsPlayer implements Player {

    private final PlayerId ownId;
    private final SplittableRandom rng;
    private final int iterations;
    private static final int C = 40;

    /**
     * @param ownId
     *            Id of the player that will be a MctsPlayer
     * @param rngSeed
     *            the seed that will be used to generate the simulated random
     *            games
     * @param iterations
     *            the number of iterations that the MCTS will execute (must be
     *            greater than the 9 and smaller than the maximal number of
     *            nodes 8355967)
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {

        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);

        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node root = new Node(state, hand.packed(),
                state.trick().playableCards(hand).size());

        for (int i = 0; i < iterations; ++i)
            this.updateNodes(root.addNode(this.ownId, C));
        return state.trick().playableCards(hand).get(root.selectChild(0));
    }

    /**
     * @param ownId
     *            Id of the current player
     * @param state
     *            current State of the Jass Game
     * @param hand
     *            hand of the player that is supposed to play
     * @return the cardset that is currently playable (for the next trick)
     */
    private static long playable(PlayerId ownId, TurnState state, long hand) {
        long cards = ownId.equals(state.nextPlayer()) ? hand
                : PackedCardSet.difference(state.packedUnplayedCards(), hand);
        return PackedTrick.playableCards(state.packedTrick(), cards);
    }

    /**
     * @param state
     *            current state of the Jass game
     * @param hand
     *            hand of the player that is supposed to play at the current
     *            trick
     * @return a rondom selected card in the hand
     */
    private int randomCard(TurnState state, long hand) {
        long cards = playable(ownId, state, hand);
        return PackedCardSet.get(cards, rng.nextInt(PackedCardSet.size(cards)));
    }

    /**
     * @param node
     *            the node from which you begin a random game
     * @return the final score after the execution of the random generated game
     */
    private long randomPlay(Node node) {
        TurnState stateForPlay = node.currentState;
        long hand = node.hand;

        while (!stateForPlay.isTerminal()) {

            while (!PackedTrick.isFull(stateForPlay.packedTrick())) {
                int card = randomCard(stateForPlay, hand);
                hand = PackedCardSet.remove(hand, card);
                stateForPlay = stateForPlay
                        .withNewCardPlayed(Card.ofPacked(card));
            }
            stateForPlay = stateForPlay.withTrickCollected();
        }
        return stateForPlay.packedScore();
    }

    /**
     * @param nodes
     *            list of nodes that you have to update the scores of
     */
    private void updateNodes(List<Node> nodes) {
        long score = randomPlay(nodes.get(0));
        for (Node node : nodes) {
//            try {
//                TeamId team = node.currentState.nextPlayer().team();
//                 node.totalPoints += PackedScore
//                         .totalPoints(score,team);
//            } catch (IllegalStateException e) {
//                //nothing
//            }
            TeamId team = node.team();
            if (team != null)
                node.totalPoints += PackedScore.totalPoints(score,
                        node.team());
            node.turnsCompleted++;
        }
    }

    /**
     * Class representing a node (state of evolution of the game) in the
     * algorithm MCTS
     * 
     * @author Jonathan Bereyziat Alexandre Santangelo
     */
    private static final class Node {

        private final TurnState currentState;
        private final Node[] children;
        private final long hand;
        private int totalPoints;
        private int turnsCompleted;

        /**
         * @param currentState
         *            State from which we initialize the node
         * @param hand
         *            hand of the next player
         * @param childSize
         *            number of cards that can be player in the hand of the next
         *            player
         */
        private Node(TurnState currentState, long hand, int childSize) {
            this.currentState = currentState;
            this.hand = hand;
            this.children = new Node[childSize];
            this.totalPoints = 0;
            this.turnsCompleted = 0;
        }

        /**
         * @param ownId
         *            the id of the player of the current state pass to the node
         * @param c
         *            constant of exploration (arbitrary chosen to be 40)
         * 
         * @return
         */
        private LinkedList<Node> addNode(PlayerId ownId, int c) {
            LinkedList<Node> pathNodes = new LinkedList<>();
            Node parent = null;
            Node node = this;
            int index = -1;

            while (node != null) {
                pathNodes.addFirst(node);
                parent = node;
                index = node.selectChild(c);
                if (index == -1)
                    return pathNodes;
                node = node.children[index];
            }

            long cards = playable(ownId, parent.currentState, parent.hand);
            int card = PackedCardSet.get(cards, index);
            long newHand = PackedCardSet.remove(parent.hand, card);

            TurnState nextTurnState = parent.currentState
                    .withNewCardPlayed(Card.ofPacked(card));

            int childSize;
            if (nextTurnState.isTerminal() || nextTurnState.trick().isFull())
                childSize = 0;
            else
                childSize = PackedCardSet
                        .size(playable(ownId, nextTurnState, newHand));

            node = new Node(nextTurnState, newHand, childSize);
            parent.children[index] = node;
            pathNodes.addFirst(node);

            return pathNodes;
        }

        /**
         * @return the team of the next player
         */
        private TeamId team() {
            int p = currentState.trick().size() - 1;
            return p >= 0 ? currentState.trick().player(p).team() : null;
        }

        /**
         * @param parent
         * @param c
         *            constant of exploration (arbitrary chosen to be 40)
         * @return a double value V that represent the value of a node regarding
         *         to the MCTS formulae
         */
        private double computeV(Node parent, int c) {
            double S = totalPoints;
            double N = turnsCompleted;

            if (turnsCompleted > 0)
                return S / N + c
                        * Math.sqrt(2 * Math.log(parent.turnsCompleted) / N);
            else
                return Double.POSITIVE_INFINITY;
        }

        /**
         * @param c
         *            constant of exploration (arbitrary chosen to be 40)
         * @return int index of the best child node
         */
        private int selectChild(int c) {
            int indexOfTheBestChild = -1;
            double bestV = Double.NEGATIVE_INFINITY;
            for (int i = 0; i < children.length; i++) {
                Node node = children[i];
                if (node == null)
                    return i;
                double nodeValue = node.computeV(this, c);
                if (nodeValue > bestV) {
                    indexOfTheBestChild = i;
                    bestV = nodeValue;
                }
            }
            return indexOfTheBestChild;
        }
    }
}