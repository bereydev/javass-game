/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.net;

/**
 * @author astra
 *
 */
public final class StringSerializer {
    
    private StringSerializer() {
        //Not instantiable. 
    }

   public static int serializeInt(int param) {
       return 0; 
   }
   
   public static int deserializeInt(int param) {
       return 0; 
   }
   
   public static long serializeLong(long param) {
       return 0; 
   }
   
   public static long deserializeLong(long param) {
       return 0; 
   }
   
   public static String serializeString(String param) {
       return "hello"; 
   }
   
   public static String deserializeString(String param) {
       return "bye"; 
   }
   
}
