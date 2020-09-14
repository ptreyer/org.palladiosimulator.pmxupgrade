package org.palladiosimulator.pmxupgrade.model.exception;

/**
 * This exception can be used to mark an invalid trace.
 * 
 * @author Andre van Hoorn
 * 
 * @since 0.95a
 */
public class PMXException extends Exception {

	private static final long serialVersionUID = 1893L;

	/**
	 * Creates a new instance of this class using the given parameters.
	 *
	 * @param message
	 *            The message of this exception.
	 */
	public PMXException(final String message) {
		super(message);
	}

	/**
	 * Creates a new instance of this class using the given parameters.
	 *
	 * @param message
	 *            The message of this exception.
	 * @param t
	 *            The cause of this exception.
	 */
	public PMXException(final String message, final Throwable t) {
		super(message, t);
	}
}
