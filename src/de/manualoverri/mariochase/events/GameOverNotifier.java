package de.manualoverri.mariochase.events;

import java.util.ArrayList;
import java.util.List;

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
