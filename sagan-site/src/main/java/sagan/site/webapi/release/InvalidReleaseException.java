package sagan.site.webapi.release;

/**
 *
 */
@SuppressWarnings("serial")
public class InvalidReleaseException extends RuntimeException {

	public InvalidReleaseException(String message) {
		super(message);
	}

}
