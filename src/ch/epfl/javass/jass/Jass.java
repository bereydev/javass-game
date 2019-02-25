package ch.epfl.javass.jass;

import java.util.Arrays;

import java.util.Collections;
import java.util.List;

import ch.epfl.javass.jass.Card.Color;

/**
 * Interface that store the constants values concerning the Jass game
 * 
 * @author Jonathan Bereyziat (282962)
 *
 */
public interface Jass {

    public static final int HAND_SIZE = 9;
    public static final int TRICKS_PER_TURN = 9;
    public static final int WINNING_POINTS = 1000;
    public static final int MATCH_ADDITIONAL_POINTS = 100;
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;
}
