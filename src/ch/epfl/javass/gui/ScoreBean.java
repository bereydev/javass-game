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
    
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? gamePoints1 : gamePoints2;
    }
    
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team==TeamId.TEAM_1 ? totalPoints1 : totalPoints2;
    }
    
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty(){
        return winningTeam; 
    }
    

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPoints1.setValue(newTurnPoints);
        else
            turnPoints2.setValue(newTurnPoints);
    }

    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1))
            totalPoints1.setValue(newTotalPoints);
        else
            totalPoints2.setValue(newTotalPoints);
    }
    public void setWinningTeam(TeamId team) {
        winningTeam.setValue(team);; 
    }
    
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            gamePoints1.setValue(newGamePoints);
        else
            gamePoints2.setValue(newGamePoints);
    }  

}
