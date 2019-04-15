/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/
package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum JassCommand {

    PLRS, TRMP, HAND, TRCK, CARD, SCOR, WINR;
    
    public static final List<JassCommand> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    public static int COUNT = 7;

}
