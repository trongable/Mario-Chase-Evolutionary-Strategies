package de.manualoverri.mariochase.gamelogic;

/**
 * User: Trong
 * Date: 7/17/2014
 * Time: 12:36 AM
 */
public interface MarioChasePlayerController {

    public int getGeneration();

    public void resume();

    public void pause();

    public void savePlayersAsIndividuals(double totalTime, double remainingTime);

    public void resetAndReloadPlayers();

    public void executeCycle();

    public void setPlayerDirections();

}
