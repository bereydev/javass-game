/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 17, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.jass;

/**
 * @author astra
 *
 */
public class MainTesterAlex {

    public static void main(String[] args) {
        
        String s = "PLYRS 2 HEBJE,NJENJJE,HBDUVBUE="; 
        
        String[] result = s.split(" "); 
        
        for(String str : result) {
            System.out.print(str+"/");
        }

    }

}
