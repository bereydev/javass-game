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
     * Private method that find the "size" of an integer bit string
     * 
     * @param bits
     *            the integer you wan't to find the size of
     * @return the size of the binary representation of the integer (based on
     *         the position of the most significant bit)
     */
    private static int bitsSize(int bits) {
        int size = 0;
        int number = bits;
        while (number != 0) {
            number = number >> 1; // divide by 2
            size++;
        }
        return size;
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
        if (size == 0)
            return 0;
        int result = ~0;
        result = result >>> Integer.SIZE - size;
        result = result << start;
        return result;
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
        if (size == 0)
            return 0;
        int result = bits;
        result = result << Integer.SIZE - size - start;
        result = result >>> Integer.SIZE - size;
        return result;
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
        Preconditions.checkArgument(
                s1 > 0 && s1 < Integer.SIZE && bitsSize(v1) <= s1);
        Preconditions.checkArgument(
                s2 > 0 && s2 < Integer.SIZE && bitsSize(v2) <= s2);
        Preconditions.checkArgument(s1 + s2 <= Integer.SIZE);
        int bits1 = v1;
        int bits2 = v2 << s1;
        int result = bits1 | bits2;
        return result;
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
        Preconditions.checkArgument(
                s1 > 0 && s1 < Integer.SIZE && bitsSize(v1) <= s1);
        Preconditions.checkArgument(
                s2 > 0 && s2 < Integer.SIZE && bitsSize(v2) <= s2);
        Preconditions.checkArgument(
                s3 > 0 && s3 < Integer.SIZE && bitsSize(v3) <= s3);
        Preconditions.checkArgument(s1 + s2 + s3 <= Integer.SIZE);
        int bits1 = v1;
        int bits2 = v2 << s1;
        int bits3 = v3 << s2 + s1;
        int result = bits1 | bits2 | bits3;
        return result;
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
        Preconditions.checkArgument(
                s1 > 0 && s1 < Integer.SIZE && bitsSize(v1) <= s1);
        Preconditions.checkArgument(
                s2 > 0 && s2 < Integer.SIZE && bitsSize(v2) <= s2);
        Preconditions.checkArgument(
                s3 > 0 && s3 < Integer.SIZE && bitsSize(v3) <= s3);
        Preconditions.checkArgument(
                s4 > 0 && s4 < Integer.SIZE && bitsSize(v4) <= s4);
        Preconditions.checkArgument(
                s5 > 0 && s5 < Integer.SIZE && bitsSize(v5) <= s5);
        Preconditions.checkArgument(
                s6 > 0 && s6 < Integer.SIZE && bitsSize(v6) <= s6);
        Preconditions.checkArgument(
                s7 > 0 && s7 < Integer.SIZE && bitsSize(v7) <= s7);
        Preconditions.checkArgument(
                s1 + s2 + s3 + s4 + s5 + s6 + s7 <= Integer.SIZE);
        int bits1 = v1;
        int bits2 = v2 << s1;
        int bits3 = v3 << s1 + s2;
        int bits4 = v4 << s1 + s2 + s3;
        int bits5 = v5 << s1 + s2 + s3 + s4;
        int bits6 = v6 << s1 + s2 + s3 + s4 + s5;
        int bits7 = v7 << s1 + s2 + s3 + s4 + s5 + s6;
        int result = bits1 | bits2 | bits3 | bits4 | bits5 | bits6 | bits7;

        return result;
    }

}
