package de.manualoverri.mariochase.gamedisplay;

import de.manualoverri.mariochase.events.GameOverListener;
import de.manualoverri.mariochase.events.Notifier;
import de.manualoverri.mariochase.gamelogic.MarioChaseHelper;
import de.manualoverri.mariochase.gamelogic.PlayerCountException;
import de.manualoverri.mariochase.gamelogic.mario.MarioPlayer;
import de.manualoverri.mariochase.gamelogic.mario.MarioPlayerController;
import de.manualoverri.mariochase.gamelogic.mario.MarioPlayerImpl;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayer;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayerController;
import de.manualoverri.mariochase.gamelogic.toad.ToadPlayerImpl;
import de.manualoverri.mariochase.learning.DbHelper;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/**
 * User: Trong
 * Date: 7/27/2014
 * Time: 6:28 PM
 */
public class MarioChasePanel extends JPanel implements KeyListener, ActionListener, GameOverListener {
    public static final int GRAPHICS_SCALE = 1;
    public static final int WIDTH = MarioChaseHelper.GAME_WIDTH;
    public static final int HEIGHT = MarioChaseHelper.GAME_HEIGHT;
    public static final int GAME_LENGTH_MS = MarioChaseHelper.GAME_LENGTH_MS;
    public static final int CYCLE_INTERVAL_MS = MarioChaseHelper.CYCLE_INTERVAL_MS;

    private Timer gameTimer;
    private int gameNumber;
    private int currentGameTimeMs = MarioChaseHelper.MARIO_START_AHEAD_MS;
    private MarioPlayerController marioController;
    private ToadPlayerController toadController;
    private MarioPlayer m1 = null;
    private ToadPlayer t1, t2, t3, t4;

    public MarioChasePanel() {
        super();
        setFocusable(true);
        requestFocus();
        init();
    }

    private void init() {
        // TODO: Get rid of this
        // Clearing out DB at start of each game because I was worried about bad data
        DbHelper.executeUpdate("delete from lu_toad_player");
        DbHelper.executeUpdate("delete from lu_toad_individual");

        gameTimer = new Timer(CYCLE_INTERVAL_MS, this);
        gameNumber = 1;
        marioController = new MarioPlayerController();
        toadController = new ToadPlayerController();
        Notifier.evolutionCompleteNotifier.addListener(toadController);
        Notifier.gameOverNotifier.addListener(this);

        try {
            m1 = MarioPlayerImpl.getInstance();
            t1 = ToadPlayerImpl.getInstance();
            t2 = ToadPlayerImpl.getInstance();
            t3 = ToadPlayerImpl.getInstance();
            t4 = ToadPlayerImpl.getInstance();
            marioController.setPlayer(m1);
            toadController.addPlayer(t1);
            toadController.addPlayer(t2);
            toadController.addPlayer(t3);
            toadController.addPlayer(t4);
        } catch (PlayerCountException e) {
            e.printStackTrace();
        }
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH * GRAPHICS_SCALE, HEIGHT * GRAPHICS_SCALE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // Paint the game panel
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, MarioChaseHelper.GAME_WIDTH, MarioChaseHelper.GAME_HEIGHT);

        // Paint the timers
        g.setColor(Color.WHITE);
        if (currentGameTimeMs >= 0) {
            g.drawString(String.format("Time remaining: %.1f", (GAME_LENGTH_MS - currentGameTimeMs) / 1000.0), 10, 20);
        } else {
            g.drawString(String.format("Head start time remaining: %.1f", -currentGameTimeMs / 1000.0), 10, 20);
        }

        // Paint some text for debugging
        g.drawString(String.format("Mario actual: %s", m1.getLocation()), 10, 40);
        g.drawString(String.format("Mario guess: %s", toadController.getCurrentMarioLocation()), 10, 60);
        g.drawString(String.format("Game number: %d", gameNumber), 650, 20);
        g.drawString(String.format("Mario generation: %d", marioController.getGeneration()), 650, 40);
        g.drawString(String.format("Toad generation: %d", toadController.getGeneration()), 650, 60);

        // Paint the players and their check ahead points
        m1.paintPlayer(g);
        t1.paintPlayer(g);
        t2.paintPlayer(g);
        t3.paintPlayer(g);
        t4.paintPlayer(g);
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyChar() == ' ') {
            marioController.resume();
            gameTimer.start();
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentGameTimeMs >= GAME_LENGTH_MS) {
            Notifier.gameOverNotifier.notifyGameOver();
        } else {
            // On each timer tick, make sure we make all of the players execute one "cycle" where the controllers
            // decide which direction to face and what action to perform
            currentGameTimeMs += CYCLE_INTERVAL_MS;
            marioController.executeCycle();

            // Game doesn't start for the toads until the initial countdown is done
            if (currentGameTimeMs == 0) {
                toadController.setMarioLocation(m1.getLocation());
                toadController.resume();
            }

            // Each cycle checks if we've won already. If not, then keep going
            if (currentGameTimeMs >= 0) {
                if (toadController.checkWinCondition()) {
                    Notifier.gameOverNotifier.notifyGameOver();
                } else {
                    toadController.executeCycle();
                }
            }
        }

        repaint();
    }

    @Override
    public void onGameOver() {
        // Save all of the players stats and reset the game
        // We have to convert time back to seconds before saving individuals
        // System.out.println("Game " + gameNumber + " over");
        gameTimer.stop();
        marioController.pause();
        marioController.savePlayersAsIndividuals(currentGameTimeMs * 0.001, (GAME_LENGTH_MS - currentGameTimeMs) * 0.001);
        marioController.resetAndReloadPlayers();

        toadController.pause();
        toadController.savePlayersAsIndividuals(currentGameTimeMs * 0.001, (GAME_LENGTH_MS - currentGameTimeMs) * 0.001);
        toadController.resetAndReloadPlayers();

        // TODO: Display stats for a few seconds before auto resuming
        repaint();

        currentGameTimeMs = MarioChaseHelper.MARIO_START_AHEAD_MS;
        marioController.resume();
        toadController.resume();
        gameNumber++;
        gameTimer.restart();

    }
}
