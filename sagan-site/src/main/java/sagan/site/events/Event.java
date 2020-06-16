package sagan.site.events;

import java.net.URI;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

@JsonDeserialize(using = EventDeserializer.class)
public class Event implements Comparable<Event> {

	private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd - HH:mm:ss z");

	private ZonedDateTime startTime;

	private ZonedDateTime endTime;

	private String summary;

	private URI link;

	private String location;

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
