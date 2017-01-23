package de.nvw_servers.exceptions;

public class AuthentificationFailedException extends Exception {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public AuthentificationFailedException(String comment) {
		super(comment);
	}
}
