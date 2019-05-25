package ch.epfl.javass.gui;

import java.util.EnumMap;
import java.util.Map;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * javaFx Bean that contains the observable Property of the Score
 *
 */
public final class ScoreBean {
    
    private final Map<TeamId, IntegerProperty> turnPoints = new EnumMap<>(TeamId.class);
    private final Map<TeamId, IntegerProperty> totalPoints = new EnumMap<>(TeamId.class);
    private final Map<TeamId, IntegerProperty> gamePoints = new EnumMap<>(TeamId.class);
    private final ObjectProperty<TeamId> winningTeam;

    public ScoreBean() {
        for (TeamId team : TeamId.ALL) {
            turnPoints.put(team,new SimpleIntegerProperty());
            totalPoints.put(team,new SimpleIntegerProperty());
            gamePoints.put(team,new SimpleIntegerProperty());
        }
        winningTeam = new SimpleObjectProperty<>();
    }

    /**
     * Allow to get the turn points of a specified team
     * 
     * @param team
     *            - the TeamId you want the points of
     * @return the turnPoints of the specified team as an
     *         ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return turnPoints.get(team);
    }

    /**
     * Allows to get the game points of a specified team
     * 
     * @param team
     *            - the TeamId you want the points of
     * @return the gamePoints of the specified team as an
     *         ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return gamePoints.get(team);
    }

    /**
     * Allows to get the total points of a specified team
     * 
     * @param team
     *            - the TeamId you want the points of
     * @return the totalPoints of the specified team as an
     *         ReadOnlyIntegerProperty
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return totalPoints.get(team);
    }

    /**
     * Allows to get the winning team of the Game
     * 
     * @return the winning TeamId as an ReadOnlyObjectProperty<TeamId>
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeam;
    }

    /**
     * Sets the new turnPoints of the specified Team for the bean
     * 
     * @param team
     *            - the TeamId you want to set the points of
     * @param newTurnPoints
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        turnPoints.get(team).set(newTurnPoints);
    }

    /**
     * Sets the new TotalPoints of the specified Team for the bean
     * 
     * @param team
     *            - the TeamId you want to set the points of
     * @param newTotalPoints
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
      totalPoints.get(team).set(newTotalPoints);
    }

    /**
     * Sets the new winningTeam for the bean
     * 
     * @param team
     *            - the new TeamId
     */
    public void setWinningTeam(TeamId team) {
        winningTeam.setValue(team);
    }

    /**
     * Sets the new gamePoints of the specified Team for the bean
     * 
     * @param team
     *            - the TeamId you want to set the points of
     * @param newGamePoints
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        gamePoints.get(team).set(newGamePoints);
    }

}
