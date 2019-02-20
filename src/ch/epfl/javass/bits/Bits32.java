package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions; 

/**
 * @author jonjb
 *
 */
public final class Bits32 {

    /**
     * 
     */
    private Bits32() {
    }
    
    /**
     * @param bits
     * @return
     */
    private static int bitsSize(int bits) {
        int size=0;
        int number = bits;
        while (number != 0) {
            number = number >> 1; //divide by 2
            size++;
        }
        return size;
    }
    
    /**
     * @param start
     * @param size
     * @return
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(size <= 32 && start >= 0 && start <= size);
        int result = ~0;
        result = result << 32 - size;
        result = result >>> start;
        return result;
    }
    
    /**
     * @param bits
     * @param start
     * @param size
     * @return
     */
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(size <= 32 && start >= 0 && start <= size);
        int result = bits;
        result = result << 32 - size + start;
        result = result >>> start;
        return result;
    }
    
    /**
     * @param v1
     * @param s1
     * @param v2
     * @param s2
     * @return
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(s1 > 0 && s1 < 32 && s2 > 0 && s2 < 32 && s1 + s2 <= 32 && bitsSize(v1) <= s1 && bitsSize(v2) <= s2);
        int low_bits = v1;
        int high_bits = v2 << 32 - s2;
        int result = low_bits | high_bits;
        return result;
    }

}
