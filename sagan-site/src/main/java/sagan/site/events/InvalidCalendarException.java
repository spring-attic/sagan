package sagan.site.events;

@SuppressWarnings("serial")
public class InvalidCalendarException extends RuntimeException {

	public InvalidCalendarException(String message) {
		super(message);
	}

	public InvalidCalendarException(String message, Throwable cause) {
		super(message, cause);
	}
}
