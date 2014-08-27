package de.manualoverri.mariochase.learning;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tnguyen on 8/7/2014.
 */
public class EvolutionCompleteNotifier {
    private List<EvolutionCompleteListener> listeners = new ArrayList<EvolutionCompleteListener>();

    public void addListener(EvolutionCompleteListener listener) {
        listeners.add(listener);
    }

    public void notifyEvolutionComplete() {
        for (EvolutionCompleteListener listener : listeners) {
            listener.onEvolutionComplete();
        }
    }
}
