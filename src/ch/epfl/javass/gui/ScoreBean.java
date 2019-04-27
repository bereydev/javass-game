package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

public final class ScoreBean {
    private int turnPoints1, turnPoints2;
    private int gamePoints1, gamePoints2;
    private int totalPoints1, totalPoints2;
    private TeamId winningTeam;

    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        if(team.equals(TeamId.TEAM_1))
            return new SimpleIntegerProperty(turnPoints1); 
        return  new SimpleIntegerProperty(turnPoints2); 
    }
    
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        if(team.equals(TeamId.TEAM_1))
            return new SimpleIntegerProperty(gamePoints1); 
        return  new SimpleIntegerProperty(gamePoints2); 
    }
    
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        if(team.equals(TeamId.TEAM_1))
            return new SimpleIntegerProperty(totalPoints1); 
        return  new SimpleIntegerProperty(totalPoints2); 
    }
    
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty(){
        return new SimpleObjectProperty<TeamId>(winningTeam); 
    }
    

    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team.equals(TeamId.TEAM_1))
            turnPoints1 = newTurnPoints;
        else
            turnPoints2 = newTurnPoints;
    }
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team.equals(TeamId.TEAM_1))
            turnPoints1 = newGamePoints;
        else
            turnPoints2 = newGamePoints;
    }
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1))
            turnPoints1 = newTotalPoints;
        else
            turnPoints2 = newTotalPoints;
    }
    public void setWinningTeam(TeamId team) {
        winningTeam = team; 
    }
    
    
    
    

}
