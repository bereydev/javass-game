/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/

/**
 * 
 */
package ch.epfl.javass.net;

import java.util.Base64; 
import java.nio.charset.StandardCharsets; 

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
       return Base64.getEncoder().encodeToString(param.getBytes(StandardCharsets.UTF_8)); 
   }
   
   public static String deserializeString(String param) {
       return new String(param.getBytes(),StandardCharsets.UTF_8); 
   }
   
   public static String combine(String...strings) {
       return String.join(",",strings); 
   }
   
   public static String[] split(String string) {
       return string.split(","); 
   }
   
}
