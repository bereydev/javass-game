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

   public static String serializeInt(int param) {
       return Integer.toUnsignedString(param);
   }
   
   public static int deserializeInt(String param) {
       return Integer.parseUnsignedInt(param);
   }
   
   public static String serializeLong(long param) {
       return Long.toUnsignedString(param);
   }
   
   public static long deserializeLong(String param) {
       return Long.parseUnsignedLong(param);
   }
   
   public static String serializeString(String param) {
       return "hello"; 
   }
   
   public static String deserializeString(String param) {
       return "bye"; 
   }
   
   public static String combine(char splitter, String...strings) {
       return "hellobye"; 
   }
   
   public static String[] split(char splitter, String string) {
       return null; 
   }
   
}
