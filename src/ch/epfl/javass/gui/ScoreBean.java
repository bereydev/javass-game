package ch.epfl.javass.gui;

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
    private IntegerProperty turnPoints1, turnPoints2;
    private IntegerProperty gamePoints1, gamePoints2;
    private IntegerProperty totalPoints1, totalPoints2;
    private ObjectProperty<TeamId> winningTeam;

    ScoreBean() {
        turnPoints1 = new SimpleIntegerProperty();
        turnPoints2 = new SimpleIntegerProperty();
        gamePoints1 = new SimpleIntegerProperty();
        gamePoints2 = new SimpleIntegerProperty();
        totalPoints1 = new SimpleIntegerProperty();
        totalPoints2 = new SimpleIntegerProperty();
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
        return team == TeamId.TEAM_1 ? turnPoints1 : turnPoints2;
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
        return team == TeamId.TEAM_1 ? gamePoints1 : gamePoints2;
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
        return team == TeamId.TEAM_1 ? totalPoints1 : totalPoints2;
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
        if (team == TeamId.TEAM_1)
            turnPoints1.setValue(newTurnPoints);
        else
            turnPoints2.setValue(newTurnPoints);
    }

    /**
     * Sets the new TotalPoints of the specified Team for the bean
     * 
     * @param team
     *            - the TeamId you want to set the points of
     * @param newTotalPoints
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team.equals(TeamId.TEAM_1))
            totalPoints1.setValue(newTotalPoints);
        else
            totalPoints2.setValue(newTotalPoints);
    }

    /**
     * Sets the new winningTeam for the bean
     * 
     * @param team
     *            - the new TeamId
     */
    public void setWinningTeam(TeamId team) {
        winningTeam.setValue(team);
        ;
    }

    /**
     * Sets the new gamePoints of the specified Team for the bean
     * 
     * @param team
     *            - the TeamId you want to set the points of
     * @param newGamePoints
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            gamePoints1.setValue(newGamePoints);
        else
            gamePoints2.setValue(newGamePoints);
    }

}
