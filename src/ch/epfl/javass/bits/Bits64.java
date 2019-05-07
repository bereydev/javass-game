
package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

public class Bits64 {

    /**
     * Private constructor you can't instantiate
     */
    private Bits64() {
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
        return size == Long.SIZE ? ~0L : (1L << size) - 1L << start;
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
        return (bits & mask(start, size)) >>> start;
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
                .checkArgument(s1 > 0 && s1 < Long.SIZE && extract(v1, 0, s1) == v1);
        Preconditions
                .checkArgument(s2 > 0 && s2 < Long.SIZE && extract(v2, 0, s2) == v2);
        Preconditions.checkArgument(s1 + s2 <= Long.SIZE);
        return v1 | v2 << s1;
    }

}
