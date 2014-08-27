package de.manualoverri.mariochase.gamelogic;

/**
 * User: Trong
 * Date: 7/21/2014
 * Time: 9:37 PM
 */
public enum MarioChasePlayerType {
    MARIO(1),
    TOAD(2);

    private final int id;

    MarioChasePlayerType(int id) {
        this.id = id;
    }

    public int getValue() {
        return id;
    }
}
