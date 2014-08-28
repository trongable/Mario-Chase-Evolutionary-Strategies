package de.manualoverri.mariochase.gamedisplay;

import de.manualoverri.mariochase.gamelogic.*;
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
import javax.swing.Timer;

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
    private int currentGameTimeMs = MarioChaseHelper.MARIO_START_AHEAD_MS;
    private MarioPlayerController marioController;
    private ToadPlayerController toadController;
    private MarioPlayer m1 = null;
    private ToadPlayer t1, t2, t3, t4;
    private GameOverNotifier gameOverNotifier;

    public MarioChasePanel() {
        super();
        setFocusable(true);
        requestFocus();
        init();
    }

    private void init() {
        DbHelper.executeUpdate("delete from lu_toad_player");
        DbHelper.executeUpdate("delete from lu_toad_individual");

        gameTimer = new Timer(CYCLE_INTERVAL_MS, this);
        marioController = new MarioPlayerController();
        toadController = new ToadPlayerController();

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

        gameOverNotifier = new GameOverNotifier();
        gameOverNotifier.addListener(this);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(WIDTH * GRAPHICS_SCALE, HEIGHT * GRAPHICS_SCALE);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.setColor(Color.DARK_GRAY);
        g.fillRect(0, 0, MarioChaseHelper.GAME_WIDTH, MarioChaseHelper.GAME_HEIGHT);

        g.setColor(Color.WHITE);
        if (currentGameTimeMs >= 0) {
            g.drawString(String.format("Time remaining: %.1f", (GAME_LENGTH_MS - currentGameTimeMs) / 1000.0), 10, 20);
        } else {
            g.drawString(String.format("Head start time remaining: %.1f", -currentGameTimeMs / 1000.0), 10, 20);
        }

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
    public void keyReleased(KeyEvent e) { }

    @Override
    public void keyTyped(KeyEvent e) { }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (currentGameTimeMs >= GAME_LENGTH_MS) {
            gameOverNotifier.notifyGameOver();
        }
        else {
            currentGameTimeMs += CYCLE_INTERVAL_MS;
            marioController.executeCycle();

            if (currentGameTimeMs == 0) {
                toadController.updateMarioLocationAfterHeadstart(m1.getLocation());
                toadController.resume();
            }

            if (currentGameTimeMs >= 0) {
                toadController.executeCycle();
                if (toadController.checkWinCondition()) {
                    gameOverNotifier.notifyGameOver();
                }
            }
        }

        repaint();
    }

    @Override
    public void onGameOver() {
        gameTimer.stop();
        marioController.pause();
        marioController.savePlayersAsIndividuals(currentGameTimeMs, GAME_LENGTH_MS);
        marioController.resetAndReloadPlayers();

        toadController.pause();
        toadController.savePlayersAsIndividuals(currentGameTimeMs, GAME_LENGTH_MS);
        toadController.resetAndReloadPlayers();

        // TODO display stats for a few seconds before auto resuming
        repaint();

        currentGameTimeMs = MarioChaseHelper.MARIO_START_AHEAD_MS;
        marioController.resume();
        toadController.resume();
        gameTimer.restart();

    }
}
