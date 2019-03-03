/**
 * 
 */
package ch.epfl.javass.jass;

import java.util.ArrayList;
import java.util.List;

import ch.epfl.javass.Preconditions;

/**
 * @author Jonathan Bereyziat
 *
 */
public class CardSet {

    private List<Card> cardList;

    /**
     * private constructor you can't instantiate
     */
    private CardSet(List<Card> cards) {
        cardList = cards;
    }

    public static final CardSet EMPTY = new CardSet(new ArrayList<Card>());
    public static final CardSet ALL_CARDS = new CardSet(allCardsList());

    private static ArrayList<Card> allCardsList() {
        long allCards = PackedCardSet.ALL_CARDS;
        ArrayList<Card> allCardsList = new ArrayList<Card>();
        for (int i = 0; i < 36; i++) {
            int card = PackedCardSet.get(allCards, i);
            allCardsList.add(Card.ofPacked(card));
        }
        return allCardsList;
    }

    /**
     * @param cards
     * List of Card object you want to create a set of
     * @return
     * A new CardSet 
     */
    public static CardSet of(List<Card> cards) {
        return new CardSet(cards);
    }

    /**
     * @param packed
     * Packed version of the set you want to create
     * @return
     * A new CardSet
     */
    public static CardSet ofPacked(long packed) {
        Preconditions.checkArgument(PackedCardSet.isValid(packed));
        ArrayList<Card> cardsList = new ArrayList<Card>();
        int length = PackedCardSet.size(packed);
        for (int i = 0; i < length; i++) {
            int card = PackedCardSet.get(packed, 0);
            cardsList.add(Card.ofPacked(card));
            PackedCardSet.remove(packed, card);
        }
        return new CardSet(cardsList);
    }

    /**
     * @return
     * the packed version of the CardSet
     */
    public long packed() {
        long pkCardSet = 0L;
        for (Card c : cardList) {
            pkCardSet = PackedCardSet.add(pkCardSet, c.packed());
        }
        return pkCardSet;
    }

    /**
     * @return
     * True if there is no card in the CardSet
     */
    public boolean isEmpty() {
        return cardList.isEmpty();
        // methode de Arraylist ou de PackedCardSet ?
    }

    /**
     * @return
     * The number of card in the CardSet
     */
    public int size() {
        return cardList.size();
    }

    /**
     * @param index
     * the position in the game of the card you wan't to get (0 is the first card of the game if the CardSet is not empty)
     * @return
     */
    public Card get(int index) {
        int card = PackedCardSet.get(this.packed(), index);
        return Card.ofPacked(card);
    }

    /**
     * @param card
     * the Card object you want to add
     * @return
     * the CardSet with the added Card
     */
    public CardSet add(Card card) {
//        this.cardList.add(card);
        
        return ofPacked(PackedCardSet.add(this.packed(), card.packed()));
//        Changer la valeur ou retourner une autre ?
    }

    /**
     * @param card
     * the Card you want to remove
     * @return
     * the CardSet withe the removed Card
     */
    public CardSet remove(Card card) {
        return ofPacked(PackedCardSet.remove(this.packed(), card.packed()));
    }

    /**
     * @param card
     * A Card object
     * @return
     * True if the card is in the CardSet
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(this.packed(), card.packed());
    }

    /**
     * @return
     * the complement of the CardSet
     */
    public CardSet complement() {
        return ofPacked(PackedCardSet.complement(this.packed()));
    }

    /**
     * @param that
     * a CardSet object
     * @return
     * the logical union of that and the CardSet
     */
    public CardSet union(CardSet that) {
        return ofPacked(PackedCardSet.union(this.packed(), that.packed()));
    }

    /**
     * @param that
     * a CardSet object
     * @return
     * the logical intersection of that and the CardSet
     */
    public CardSet intersection(CardSet that) {
        return ofPacked(PackedCardSet.intersection(this.packed(), that.packed()));
    }

    /**
     * @param that
     * a CardSet object
     * @return
     * the difference between the CardSet and that
     */
    public CardSet difference(CardSet that) {
        return ofPacked(PackedCardSet.difference(this.packed(), that.packed()));
    }

    /**
     * @param color
     * a Color object (color of the cards you want to get)
     * @return
     * the subset of the color "color"
     */
    public CardSet subsetOfColor(Card.Color color) {
        return ofPacked(PackedCardSet.subsetOfColor(this.packed(), color));
    }

}
