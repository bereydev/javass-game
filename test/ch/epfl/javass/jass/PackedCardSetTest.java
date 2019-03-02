package ch.epfl.javass.jass;

import ch.epfl.javass.bits.Bits64;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class PackedCardSetTest {

//    @Test
//    void isValidWorksForAllValidSet() {
//        for (int i = 1; i < 512; ++i) {
//            for (int j = 1; j < 512; ++j) {
//                for (int k = 1; k < 512; ++k) {
//                    for (int l = 1; l < 512; ++l) {
//
//                        assertTrue(PackedCardSet.isValid(i | j << 16 | k << 32 | l << 48));
//                    }
//                }
//            }
//        }
//    }

    @Test
    void trumpAbove() {
        for (int c = 0; c < 4; ++c) {
            for (int r = 0; r < 9; ++r) {
                long cards = PackedCardSet.trumpAbove(c << 4 | r);
                for (int i = 0; i < PackedCardSet.size(cards); ++i) {
                    assertTrue(PackedCard.isBetter(Card.Color.values()[c], PackedCardSet.get(cards, i), c << 4 | r));
                }
            }
        }
    }

    @Test
    void singletonWorksForAllValidCards() {
        for (int c = 0; c < 4; ++c) {
            for (int r = 0; r < 9; ++r) {
                int pkCard = c << 4 | r;
                long singleton = PackedCardSet.singleton(pkCard);
                assertEquals(1L << c * 16 + r, singleton);

            }
        }

    }

    @Test
    void isEmptyWorks() {
        assertTrue(PackedCardSet.isEmpty(0L));
    }

    @Test
    void size() {
    }

    @Test
    void getWorks() {
        long l = PackedCardSet.ALL_CARDS;
        for (int c = 0; c < 4; ++c) {
            for (int r = 0; r < 9; ++r) {
                assertEquals(c << 4 | r, PackedCardSet.get(l, c * 9 + r));
            }
        }
    }


    @Test
    void contains() {
    }

//    @Test
//    void subsetOfColorWorks() {
//        for (int c = 0; c < 4; ++c) {
//            for (int i = 1; i < 512; ++i) {
//                for (int j = 1; j < 512; ++j) {
//                    for (int k = 1; k < 512; ++k) {
//                        for (int l = 1; l < 512; ++l) {
//                            long cardSet = i | j << 16 | k << 32 | l << 48;
//                            assertEquals(cardSet & Bits64.mask(c * 16, 9), PackedCardSet.subsetOfColor(cardSet, Card.Color.values()[c]));
//                        }
//                    }
//                }
//            }
//        }
//    }

    @Test
    void toStringWorks() {
        long s = PackedCardSet.EMPTY;
        int c1 = PackedCard.pack(Card.Color.HEART, Card.Rank.SIX);
        int c2 = PackedCard.pack(Card.Color.SPADE, Card.Rank.ACE);
        int c3 = PackedCard.pack(Card.Color.SPADE, Card.Rank.SIX);
        s = PackedCardSet.add(s, c1);
        s = PackedCardSet.add(s, c2);
        s = PackedCardSet.add(s, c3);
        assertEquals("{♠6,♠A,♡6}",PackedCardSet.toString(s));
    }
}