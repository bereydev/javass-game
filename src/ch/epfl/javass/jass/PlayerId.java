/**
 *	Author : Alexandre Santangelo 
 *	Date   : Feb 25, 2019	
*/

package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public enum PlayerId {

    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));

    public static final int COUNT = 4;

    /**
     * @return The team a player belongs to
     */
    public TeamId team() {
        if (this.ordinal() % TeamId.COUNT == 0)
            return TeamId.TEAM_1;

        return TeamId.TEAM_2;
    }

}
