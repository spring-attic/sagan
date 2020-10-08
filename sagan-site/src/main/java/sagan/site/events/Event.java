package sagan.site.events;

import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Event implements Comparable<Event> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss z");

	private static final Pattern URL_REGEXP = Pattern.compile("http[^\\s<\"]*");

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	private String summary;

	private URI link;

	private String location;

	public Event(GoogleCalendarEvent event) {
		this.startTime = parseEventDate(event.getStart());
		this.endTime = parseEventDate(event.getEnd());
		this.summary = event.getSummary();
		this.location = event.getLocation();
		this.link = parseLink(event.getDescription());
	}

	private ZonedDateTime parseEventDate(GoogleCalendarEvent.EventDate eventDate) {
		if (eventDate.getDateTime() != null) {
			return ZonedDateTime.parse(eventDate.getDateTime());
		}
		return ZonedDateTime.of(LocalDate.parse(eventDate.getDate()), LocalTime.MIDNIGHT, ZoneId.systemDefault());
	}

	private URI parseLink(String description) {
		try {
			Matcher matcher = URL_REGEXP.matcher(description);
			String url = matcher.find() ? matcher.group() : "#";
			return URI.create(url);
		}
		catch (IllegalArgumentException ex) {
			return URI.create("#");
		}
	}

	public Event() {
	}

	public ZonedDateTime getStartTime() {
		return this.startTime;
	}

	public void setStartTime(ZonedDateTime startTime) {
		this.startTime = startTime;
	}

	public String formatStartTime() {
		return FORMATTER.format(this.startTime);
	}

	public ZonedDateTime getEndTime() {
		return this.endTime;
	}

	public void setEndTime(ZonedDateTime endTime) {
		this.endTime = endTime;
	}

	public String formatEndTime() {
		return FORMATTER.format(this.endTime);
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public URI getLink() {
		return this.link;
	}

	public void setLink(URI link) {
		this.link = link;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	@Override
	public int compareTo(Event other) {
		return this.startTime.compareTo(other.startTime);
	}
}
