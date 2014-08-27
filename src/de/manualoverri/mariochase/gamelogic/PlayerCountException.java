package de.manualoverri.mariochase.gamelogic;

/**
 * User: Trong
 * Date: 7/18/2014
 * Time: 8:02 PM
 */
public class PlayerCountException extends Exception {

    public PlayerCountException() {
        super();
    }

    public PlayerCountException(String message) {
        super(message);
    }

    public PlayerCountException(String message, Throwable cause) {
        super(message, cause);
    }

    public PlayerCountException(Throwable cause) {
        super(cause);
    }
}
