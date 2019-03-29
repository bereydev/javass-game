package ch.epfl.javass.jass;
 
import java.util.LinkedList;
import java.util.List;
import java.util.SplittableRandom;
 
import ch.epfl.javass.Preconditions;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;
 
/**
 * Class representing a simulated player using the MCTS algorithm
 *
 * @author Jonathan Bereyziat
 * @author Alexandre Santangelo
 */
public final class MctsPlayer implements Player {
    //méthodes de test
    public static void main(String[] args) { //TODO Enlever pour le rendu
        
        MctsPlayer player = new MctsPlayer(PlayerId.PLAYER_2, 0, 9);
       
        CardSet hand = CardSet.EMPTY
                .add(Card.of(Color.SPADE, Rank.EIGHT))
                .add(Card.of(Color.SPADE, Rank.NINE))
                .add(Card.of(Color.SPADE, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.SIX))
                .add(Card.of(Color.HEART, Rank.SEVEN))
                .add(Card.of(Color.HEART, Rank.EIGHT))
                .add(Card.of(Color.HEART, Rank.NINE))
                .add(Card.of(Color.HEART, Rank.TEN))
                .add(Card.of(Color.HEART, Rank.JACK));
       
        CardSet unsedCards = CardSet.ALL_CARDS.difference(hand).remove(Card.of(Color.SPADE, Rank.JACK));
       
        TurnState state = TurnState.ofPackedComponents(
                PackedScore.INITIAL,
                unsedCards.packed(),
                Trick.firstEmpty(Color.SPADE, PlayerId.PLAYER_1).withAddedCard(Card.of(Color.SPADE, Rank.JACK)).packed());
       
        Node root = new Node(state, hand.packed(), state.trick().playableCards(hand).size());
       
        long time = System.currentTimeMillis();
        for (int i = 0; i < 100_000; ++i) {
            player.updateNodes(root.addNode(player.ownId, 40));
        }
        System.out.println(System.currentTimeMillis() - time);
        System.out.println(nodeTreeString(root, 1));
    }
    
    private static String nodeTreeString(Node root, int maxDeep) {
        StringBuilder sb = new StringBuilder();
        addNodeRecursif(sb, root, 0, maxDeep);
 
        return sb.toString();
    }
   
    private static void addNodeRecursif(StringBuilder sb, Node root, int deep, int maxDeep) {
        addNodeTree(sb, root, deep);
        if (root == null || deep == maxDeep)
            return;
       
        for (Node child : root.children)
            addNodeRecursif(sb, child, deep + 1, maxDeep);
    }
   
    private static void addNodeTree(StringBuilder sb, Node node, int deep) {
        if (node == null)
            return;
       
        for (int i = 0; i < deep; ++i)
            sb.append('\t');
        sb.append(node).append('\n');
    }
    //fin des méthodes de test
   
   
    private final PlayerId ownId;
    private final SplittableRandom rng;
    private final int iterations;
    private static final int C = 40;
   
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
       
        Preconditions.checkArgument(iterations >= Jass.HAND_SIZE);
        Preconditions.checkArgument(iterations < (Integer.MAX_VALUE / 257));
       
        this.ownId = ownId;
        this.rng = new SplittableRandom(rngSeed);
        this.iterations = iterations;
    }
   
    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        Node root = new Node(state, hand.packed(), state.trick().playableCards(hand).size());
       
        for (int i = 0; i < iterations; ++i)
            this.updateNodes(root.addNode(this.ownId, C));
       
        return hand.get(root.selectChild(0));
    }
   
   
    private static long playable(PlayerId ownId, TurnState state, long hand) {
        long cards = ownId.equals(state.nextPlayer()) ? hand : PackedCardSet.difference(state.packedUnplayedCards(), hand);
        return PackedTrick.playableCards(state.packedTrick(), cards);
    }
   
    private int randomCard(TurnState state, long hand) {
        long cards = playable(ownId, state, hand);
        return PackedCardSet.get(cards, rng.nextInt(PackedCardSet.size(cards)));
    }
 
    private long randomPlay(Node node) {
        TurnState stateForPlay = node.currentState;
        long hand = node.hand;
       
        while (!stateForPlay.isTerminal()) {
           
            while(!PackedTrick.isFull(stateForPlay.packedTrick())) {
                int card = randomCard(stateForPlay, hand);
                hand = PackedCardSet.remove(hand, card);
                stateForPlay = stateForPlay.withNewCardPlayed(Card.ofPacked(card));
            }
            stateForPlay = stateForPlay.withTrickCollected();
        }
        return stateForPlay.packedScore();
    }
   
   
    private void updateNodes(List<Node> nodes) {
        long score = randomPlay(nodes.get(0));
        for (Node node2 : nodes) {
            TeamId team = node2.team();
            if (team != null)
                node2.totalPoints += PackedScore.totalPoints(score, node2.team());
            node2.turnsCompleted++;
        }
    }
   
   
    private static class Node {
       
        private final TurnState currentState;
        private final Node[] children;
        private final long hand;
        private int totalPoints;
        private int turnsCompleted;
       
        private Node(TurnState currentState, long hand, int childSize) {
            this.currentState = currentState;
            this.hand = hand;
            this.children = new Node[childSize];
            this.totalPoints = 0;
            this.turnsCompleted = 0;
        }
        
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
           
            TurnState nextTurnState = parent.currentState.withNewCardPlayed(Card.ofPacked(card));
           
            int childSize;
            if (nextTurnState.isTerminal() || nextTurnState.trick().isFull())
                childSize = 0;
            else
                childSize = PackedCardSet.size(playable(ownId, nextTurnState, newHand));
           
            node = new Node(nextTurnState, newHand, childSize);
            parent.children[index] = node;
            pathNodes.addFirst(node);
           
            return pathNodes;
        }
       
        private TeamId team() {
            int p = currentState.trick().size() - 1;
            return p >= 0 ? currentState.trick().player(p).team() : null;
        }
       
        private int nbrOfChild() {
            int i = 0;
            for (Node child : children) {
                if (child != null)
                    ++i;
            }
            return i;
        }
       
        private double computeV(Node parent, int c) {
            double S= totalPoints;
            double N = turnsCompleted;
           
            if (turnsCompleted > 0)
                return S / N + c * Math.sqrt(2 *Math.log(parent.turnsCompleted) / N);
            else
                return Double.POSITIVE_INFINITY;
        }
       
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
 
        @Override
        public String toString() {
            return new StringBuilder()
            .append("Node : [")
            .append(currentState.trick())
            .append('\t').append(PackedCardSet.toString(hand))
            .append('\t').append("child : ").append(nbrOfChild()).append('/').append(children.length)
            .append('\t').append("totalPoints : ").append((float)totalPoints / (float)turnsCompleted)
            .append('\t').append("turnsCompleted : ").append(turnsCompleted)
            .append("]").toString();
        }
    }
}