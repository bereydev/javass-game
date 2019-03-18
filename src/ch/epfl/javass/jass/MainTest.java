/*
 *	Author : Alexandre Santangelo 
 *	Date   : Mar 16, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

/**
 * @author astra
 *
 */
public class MainTest {

    /**
     * @param args
     */
    public static void main(String[] args) {
        int pkCard=0; 
        for(int i=0; i<4; i++) {
            for(int j=0; j<9; j++) {
                pkCard= j + (i<<4); 
            }
        }

    }

}
