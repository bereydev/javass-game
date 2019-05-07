package ch.epfl.javass.bits;

import ch.epfl.javass.Preconditions;

/**
 * @author Jonathan Bereyziat (282962)
 *
 */
public final class Bits32 {

    /**
     * Private constructor you can't instantiate
     */
    private Bits32() {
    }

    /**
     * Public method that give an integer which binary representation is '1'
     * between start(include) and size(exclude) and '0' otherwise
     * 
     * @param start
     *            the index from which you want to start the mask
     * @param size
     *            the number of position you wan't to mask with '1'
     * @return the processed integer
     */
    public static int mask(int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Integer.SIZE);
        Preconditions.checkArgument(size >= 0 && size <= Integer.SIZE - start);
        return size == Integer.SIZE ? ~0 : (1 << size) - 1 << start;
    }

    /**
     * Public method that extract a portion of bits from a bitstring
     * 
     * @param bits
     *            the bitstring in which you wan't to extract some bits
     * @param start
     *            the index from which you wan't to start the extraction
     * @param size
     *            the number of bits you wan't to extract
     * @return
     */
    public static int extract(int bits, int start, int size) {
        Preconditions.checkArgument(start >= 0 && start <= Integer.SIZE);
        Preconditions.checkArgument(size >= 0 && size <= Integer.SIZE - start);
        return size == 0 ? 0 : (bits & mask(start, size)) >>> start;
    }

    /**
     * Public method that allow to concatenate 2 integers in a unique bitstring
     * 
     * @param v1
     *            the integer that takes the s1 least significant bits
     * @param s1
     *            the length of the 1st integer to concatenate
     * @param v2
     *            the integer that takes the s2 next bits
     * @param s2
     *            the length of the 2nd integer to concatenate
     * @return the concatenated integer 'v1 + v2'
     */
    public static int pack(int v1, int s1, int v2, int s2) {
        Preconditions.checkArgument(s1 + s2 <= Integer.SIZE);
        Preconditions.checkArgument(
                s1 > 0 && s1 < Integer.SIZE && extract(v1, 0, s1) == v1);
        Preconditions.checkArgument(
                s2 > 0 && s2 < Integer.SIZE && extract(v2, 0, s2) == v2);
        return v1 | v2 << s1;
    }

    /**
     * Public method that allow to concatenate 3 integers in a unique bitstring
     * 
     * @param v1
     *            the integer that takes the s1 least significant bits
     * @param s1
     *            the length of the 1st integer to concatenate
     * @param v2
     *            the integer that takes the s2 next bits
     * @param s2
     *            the length of the 2nd integer to concatenate
     * @param v3
     *            the integer that takes the s3 next bits
     * @param s3
     *            the length of the 3rd integer to concatenate
     * @return the concatenated integer 'v1 + v2 + v3'
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3) {
        return pack(pack(v1, s1, v2, s2), s1 + s2, v3, s3);
    }

    /**
     * Public method that allow to concatenate 7 integers in a unique bitstring
     * 
     * @param v1
     *            the integer that takes the s1 least significant bits
     * @param s1
     *            the length of the 1st integer to concatenate
     * @param v2
     *            the integer that takes the s2 next bits
     * @param s2
     *            the length of the 2nd integer to concatenate
     * @param v3
     *            the integer that takes the s3 next bits
     * @param s3
     *            the length of the 3rd integer to concatenate
     * @param v4
     *            the integer that takes the s4 next bits
     * @param s4
     *            the length of the 4th integer to concatenate
     * @param v5
     *            the integer that takes the s5 next bits
     * @param s5
     *            the length of the 5th integer to concatenate
     * @param v6
     *            the integer that takes the s6 next bits
     * @param s6
     *            the length of the 6th integer to concatenate
     * @param v7
     *            the integer that takes the s7 next bits
     * @param s7
     *            the length of the 7th integer to concatenate
     * @return the concatenated integer 'v1 + v2 + v3 + v4 + v5 + v6 + v7'
     */
    public static int pack(int v1, int s1, int v2, int s2, int v3, int s3,
            int v4, int s4, int v5, int s5, int v6, int s6, int v7, int s7) {
        int bits1 = pack(v1, s1, v2, s2, v3, s3);
        int bits2 = pack(v4, s4, v5, s5, v6, s6);
        int bits3 = pack(bits1, s1 + s2 + s3, bits2, s4 + s5 + s6);

        return pack(bits3, s1 + s2 + s3 + s4 + s5 + s6, v7, s7);
    }

}
