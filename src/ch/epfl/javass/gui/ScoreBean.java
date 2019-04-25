package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;

public final class ScoreBean {
    private int turnPoints1, turnPoints2;
    private int gamePoints1, gamePoints2;
    private int totalPoints1, totalPoints2;
    private TeamId winningTeam;

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
    }

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPoints1 = newTurnPoints;
        else
            turnPoints2 = newTurnPoints;
    }
    
    

}
