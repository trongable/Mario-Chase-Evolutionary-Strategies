package de.manualoverri.mariochase.events;

import java.util.List;
import java.util.ArrayList;

/**
 * User: Trong
 * Date: 8/4/2014
 * Time: 10:21 PM
 */
public class GameOverNotifier {

    private List<GameOverListener> listeners = new ArrayList<GameOverListener>();

    public void addListener(GameOverListener listener) {
        listeners.add(listener);
    }

    public void notifyGameOver() {
        for (GameOverListener listener : listeners) {
            listener.onGameOver();
        }
    }
}
