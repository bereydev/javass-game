/*
 *	Author : Alexandre Santangelo & Jonathan Bereyziat
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
        // Not instantiable.
    }

    /**
     * Serializes an integer as a String
     * 
     * @param param
     *            - the integer to serialize
     * @return the serialize String value
     */
    public static String serializeInt(int param) {
        return Integer.toUnsignedString(param);
    }

    /**
     * Deserializes an integer previously serialized into a String
     * 
     * @param param
     *            - the string that encodes the integer
     * @return the deserialized int value
     */
    public static int deserializeInt(String param) {
        return Integer.parseUnsignedInt(param);
    }

    /**
     * Serializes a long as a String
     * 
     * @param param
     *            - the long to serialize
     * @return the serialized String value
     */
    public static String serializeLong(long param) {
        return Long.toUnsignedString(param);
    }

    /**
     * Deserializes a long previously serialized into a String
     * 
     * @param param
     *            - the String that encodes the long value
     * @return the deserialized long value
     */
    public static long deserializeLong(String param) {
        return Long.parseUnsignedLong(param);
    }

    /**
     * Serializes a String into an encode UTF_8 String
     * 
     * @param param
     *            - the plaintext String
     * @return the encoded String
     */
    public static String serializeString(String param) {
        return Base64.getEncoder()
                .encodeToString(param.getBytes(StandardCharsets.UTF_8));
    }

    /**
     * Deserialized a String previously encoded with UTF_8 serializer
     * 
     * @param param
     *            - the encoded String
     * @return the plaintext String
     */
    public static String deserializeString(String param) {
        return new String(Base64.getDecoder().decode(param),
                StandardCharsets.UTF_8);
    }

    /**
     * Combine with a comma "," delimiter a list of String values
     * 
     * @param strings
     *            - multiple arguments of type String
     * @return a unique String combination
     */
    public static String combine(String... strings) {
        return String.join(",", strings);
    }

    /**
     * Splits a String around matches of comma ","
     * 
     * @param string
     *            - the single string to split
     * @return an array of String values
     */
    public static String[] split(String string) {
        return string.split(",");
    }

}
