/**
 * 
 */
package com.aimir.fep.protocol.fmp.exception;

/**
 * @author simhanger
 *
 */
public class FMPNEGRTimeoutException extends FMPException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * constructor
	 */
	public FMPNEGRTimeoutException() {
		super();
	}

	/**
	 * constructor
	 *
	 * @param msg
	 *            <code>String</code> message
	 */
	public FMPNEGRTimeoutException(String msg) {
		super(msg);
	}

}
