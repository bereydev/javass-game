/**
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/
package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum TeamId {
    TEAM_1, TEAM_2;
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    public static final int COUNT = 2;

    public TeamId other() {
        if (this.ordinal() == 0) {
            return TEAM_2;
        } else {
            return TEAM_1;
        }
    }
}
