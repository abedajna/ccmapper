package com.abedajna.cccmapper.transactionManager;

public class TooManyResultsException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public TooManyResultsException(String message) {
		super(message);
	}

	public TooManyResultsException() {
		super();
	}
}
