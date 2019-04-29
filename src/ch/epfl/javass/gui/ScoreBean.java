package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;

public final class ScoreBean {
    private IntegerProperty turnPoints1, turnPoints2;
    private IntegerProperty gamePoints1, gamePoints2;
    private IntegerProperty totalPoints1, totalPoints2;
    private ObjectProperty<TeamId> winningTeam;

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? turnPoints1 : turnPoints2;
    }

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPoints1.setValue(newTurnPoints);
        else
            turnPoints2.setValue(newTurnPoints);
    }
    
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? gamePoints1 : gamePoint2;
    }
    
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            turnPoints1.setValue(newGamePoints);
        else
            turnPoints2.setValue(newGamePoints);
    }
    
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }
    
    
    

}
