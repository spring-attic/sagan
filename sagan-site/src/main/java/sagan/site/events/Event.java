package sagan.site.events;

import java.net.URI;
import java.time.ZonedDateTime;
import java.util.TimeZone;

import biweekly.component.VEvent;

public class Event {

	private final ZonedDateTime startTime;

	private final ZonedDateTime endTime;

	private final String summary;

	private final URI link;

	private final String location;

	protected Event(ZonedDateTime startTime, ZonedDateTime endTime, String summary, URI link, String location) {
		this.startTime = startTime;
		this.endTime = endTime;
		this.summary = summary;
		this.link = link;
		this.location = location;
	}

	protected Event(VEvent event, TimeZone timeZone) {
		this.startTime = ZonedDateTime.ofInstant(event.getDateStart().getValue().toInstant(), timeZone.toZoneId());
		this.endTime = ZonedDateTime.ofInstant(event.getDateEnd().getValue().toInstant(), timeZone.toZoneId());
		this.summary = event.getSummary().getValue();
		this.link = parseLink(event);
		this.location = event.getLocation().getValue();
	}

	private URI parseLink(VEvent event) {
		try {
			return URI.create(event.getDescription().getValue());
		}
		catch (IllegalArgumentException ex) {
			return URI.create("#");
		}
	}

	public ZonedDateTime getStartTime() {
		return startTime;
	}

	public ZonedDateTime getEndTime() {
		return endTime;
	}

	public String getSummary() {
		return summary;
	}

	public URI getLink() {
		return link;
	}

	public String getLocation() {
		return location;
	}

}
