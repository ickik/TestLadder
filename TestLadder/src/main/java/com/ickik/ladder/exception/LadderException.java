package com.ickik.ladder.exception;

/**
 * Exception of the project which is throw every time an error appears.
 * @author Patrick Allgeyer, p.allgeyer@gmail.com
 * @version 0.1.000, 08/03/11
 */
@SuppressWarnings("serial")
public class LadderException extends Exception {

	public LadderException(String message) {
		super(message);
	}
	
	public LadderException(String message, Exception exception) {
		super(message, exception);
	}
}
