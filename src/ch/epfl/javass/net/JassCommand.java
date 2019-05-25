/*
 *	Author : Alexandre Santangelo 
 *	Date   : Apr 15, 2019	
*/
package ch.epfl.javass.net;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enum that stocks the different Jass command used in the Remote/Serveur
 * implementation this commands are mainly used to communicate between the Main
 * Game and the Remote players it allows the RemotePlayer to start the different
 * methods of the Player interface
 */
public enum JassCommand {

    PLRS, TRMP, HAND, TRCK, CARD, SCOR, WINR, SETRMP;

    public static final List<JassCommand> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    public static final int COUNT = 8;

}
