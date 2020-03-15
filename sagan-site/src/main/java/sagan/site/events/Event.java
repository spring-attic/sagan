package sagan.site.events;

import java.net.URI;
import java.time.Duration;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import biweekly.component.VEvent;

public class Event implements Comparable<Event> {

	private static final Pattern URL_REGEXP = Pattern.compile("http[^\\s<\"]*");

	private final LocalDate firstDay;

	private final LocalDate lastDay;

	private final String summary;

	private final URI link;

	private final String location;

	protected Event(LocalDate firstDay, LocalDate lastDay, String summary, URI link, String location) {
		this.firstDay = firstDay;
		this.lastDay = lastDay;
		this.summary = summary;
		this.link = link;
		this.location = location;
	}

	protected Event(VEvent event) {
		this.firstDay = event.getDateStart().getValue().toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
		this.lastDay = event.getDateEnd().getValue().toInstant().minus(Duration.ofMinutes(1)).atZone(ZoneId.systemDefault()).toLocalDate();
		this.summary = event.getSummary().getValue();
		this.link = parseLink(event);
		this.location = event.getLocation().getValue();
	}

	private URI parseLink(VEvent event) {
		try {
			Matcher matcher = URL_REGEXP.matcher(event.getDescription().getValue());
			String url = matcher.find() ? matcher.group() : "#";
			return URI.create(url);
		}
		catch (IllegalArgumentException ex) {
			return URI.create("#");
		}
	}

	public LocalDate getFirstDay() {
		return this.firstDay;
	}

	public LocalDate getLastDay() {
		return this.lastDay;
	}

	public boolean isSingleDayEvent() {
		return this.firstDay.equals(this.lastDay);
	}

	public String getSummary() {
		return this.summary;
	}

	public URI getLink() {
		return this.link;
	}

	public String getLocation() {
		return this.location;
	}

	@Override
	public int compareTo(Event other) {
		return this.firstDay.compareTo(other.firstDay);
	}
}
