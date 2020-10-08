package sagan.site.events;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleCalendar {

	private String summary;

	private List<GoogleCalendarEvent> events;

	@JsonCreator
	public GoogleCalendar(@JsonProperty("summary") String summary, @JsonProperty("items") List<GoogleCalendarEvent> events) {
		this.summary = summary;
		this.events = events;
	}

	public String getSummary() {
		return summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public List<GoogleCalendarEvent> getEvents() {
		return events;
	}

	public void setEvents(List<GoogleCalendarEvent> events) {
		this.events = events;
	}
}
