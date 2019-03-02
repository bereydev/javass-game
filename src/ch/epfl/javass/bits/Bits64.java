
package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

public class Bits64 {
    
    /**
     * Private constructor you can't instantiate
     */
    private Bits64() {
    }

    /**
     * Private method that find the "size" of an integer bit string
     * 
     * @param bits
     *            the integer you wan't to find the size of
     * @return the size of the binary representation of the integer (based on
     *         the position of the most significant bit)
     */
    private static int bitsSize(long bits) {
        int size = 0;
        long number = bits;
        while (number != 0) {
            number = number >> 1; // divide by 2
            size++;
        }
        return size;
    }

    /**
     * @param start
     *            Where to put the first 1
     * @param size
     *            The number of 1's
     * @return A bit chain where all bits are 0 except for the size bits that
     *         follow the #start bit
     */
    public static long mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(size >= 0 && size <= Long.SIZE - start);
        if (size == Long.SIZE)
            return ~0L;

        long num = 1L << size;
        num -= 1L;
        num = num << start;
        return num;
    }

    /**
     * @param bits
     *            The bits you wish to extract your bit chain from
     * @param start
     *            The start of the bit chain you wish to extract
     * @param size
     *            The size of the bit chain
     * @return The bit chain starting at start of size 'size' from 'bits'
     */
    public static long extract(long bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Long.SIZE);
        Preconditions.checkArgument(size >= 0 && size <= Long.SIZE - start);

        bits = bits & mask(start, size);
        bits = bits >>> start;

        return bits;
    }

    /**
     * @param v1
     *            The number that occupies the first bits
     * @param s1
     *            The size that the #1 number should occupy
     * @param v2
     *            The number that follows v1
     * @param s2
     *            The size that the #2 number should occupy
     * @return A packed version of v1 and v2 where v1 occupies the first bits
     */
    public static long pack(long v1, int s1, long v2, int s2) {
        Preconditions
                .checkArgument(s1 > 0 && s1 < Long.SIZE && bitsSize(v1) <= s1);
        Preconditions
                .checkArgument(s2 > 0 && s2 < Long.SIZE && bitsSize(v2) <= s2);
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);

        long bits = v2 << s1;

        return v1 | bits;
    }

}
