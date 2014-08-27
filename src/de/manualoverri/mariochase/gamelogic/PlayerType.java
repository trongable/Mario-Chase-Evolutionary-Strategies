package de.manualoverri.mariochase.gamelogic;

/**
 * User: Trong
 * Date: 7/17/2014
 * Time: 12:40 AM
 */
public enum PlayerType {
    MARIO(0), TOAD(1);

    private final int value;

    private PlayerType(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
