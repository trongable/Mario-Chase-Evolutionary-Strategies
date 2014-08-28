package de.manualoverri.mariochase.gamelogic.mario;

import de.manualoverri.mariochase.gamelogic.MarioChasePlayerController;
import de.manualoverri.mariochase.gamelogic.Point;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayer;
import de.manualoverri.mariochase.learning.EvolutionCompleteListener;

import java.util.ArrayList;
import java.util.List;

/**
 * User: Trong
 * Date: 8/13/2014
 * Time: 11:15 PM
 */
public class MarioPlayerController implements MarioChasePlayerController, EvolutionCompleteListener {

    private MarioPlayer player;
    private int generation;
    private boolean running;
    int currentCycle;

    public MarioPlayerController() {
        player = null;
        generation = 0;
        currentCycle = 0;
        running = false;
    }

    public void setPlayer(MarioPlayer player) {
        this.player = player;
    }

    @Override
    public int getGeneration() {
        return 0;
    }

    @Override
    public void resume() {
        running = true;
    }

    @Override
    public void pause() {
        running = false;

    }

    @Override
    public void savePlayersAsIndividuals(double totalTime, double remainingTime) {
        player.saveAsIndividual(generation, totalTime, remainingTime);
    }

    @Override
    public void resetAndReloadPlayers() {
        player.reset();
        player.rerandomize();
    }

    @Override
    public void executeCycle() {
        if (!running) {
            return;
        }

        if (currentCycle % 5 == 0) {
            setPlayerDirections();
        }

        player.step();
        currentCycle++;
    }

    @Override
    public void setPlayerDirections() {
        player.setDirection((int) (Math.random() * 360));
    }

    @Override
    public void onEvolutionComplete() {

    }
}
