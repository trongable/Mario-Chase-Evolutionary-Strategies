package de.manualoverri.mariochase.gamedisplay;

import javax.swing.*;

/**
 * User: Trong
 * Date: 7/27/2014
 * Time: 6:54 PM
 */
public class MarioChaseGame {

    /**
     * Launches the game window
     *
     * @param args Unused arguments to launch the game
     */
    public static void main(String args[]) {
        JFrame window = new JFrame("Mario Chase Learning with Evolutionary Strategies");
        MarioChasePanel game = new MarioChasePanel();
        window.setContentPane(game);
        window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        window.setResizable(false);
        window.pack();
        window.setVisible(true);
        game.addKeyListener(game);
    }
}
