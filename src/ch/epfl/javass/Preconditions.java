package ch.epfl.javass;

/**
 * @author jonjb
 *
 */
public final class Preconditions {

    /**
     * 
     */
    private Preconditions() {
    }
    
    /**
     * @param b
     */
    public static void checkArgument(boolean b) {
        if (!b) {
            throw new IllegalArgumentException();
        }
    }
    
    /**
     * @param index
     * @param size
     * @return
     */
    public static int checkIndex(int index, int size) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException();
        }
        else {
            return index;
        }
    }
    

}
