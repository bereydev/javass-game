package ch.epfl.javass;

/**
 * @author Jonathan Bereyziat (282962)
 *
 */
public final class Preconditions {

    /**
     * private constructor you can't instantiate
     */
    private Preconditions() {
    }

    /**
     * Check if the condition passed to the method is True or False
     * 
     * @param b
     *            the boolean condition to check
     * @throws IllegalArgumentException
     *             if the boolean is false
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }

    /**
     * @param index
     *            the index to check
     * @param size
     *            the size of the entity to check
     * @return the index if it's not negative and not smaller than the size
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        } else {
            return index;
        }
    }

}
