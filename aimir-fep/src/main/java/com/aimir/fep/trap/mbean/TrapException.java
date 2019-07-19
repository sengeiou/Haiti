package com.aimir.fep.trap.mbean;

public class TrapException extends Exception {

	private static final long serialVersionUID = 5646664833866052073L;

	public TrapException() {
        super();
    }

    public TrapException(String message) {
        super(message);
    }

    public TrapException(Throwable t) {
        super(t.toString());
    }

    public static int ERROR_NOTIFICATION_LISTENER_RESUME = 1;
}
