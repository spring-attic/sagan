package sagan.site.events;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleCalendarEvent {

	private EventDate start;
	private EventDate end;
	private String summary;
	private String location;
	private String description;

	@JsonCreator
	public GoogleCalendarEvent(@JsonProperty("start") EventDate start, @JsonProperty("end") EventDate end,
			@JsonProperty("summary") String summary, @JsonProperty("location") String location,
			@JsonProperty("description") String description) {
		this.start = start;
		this.end = end;
		this.summary = summary;
		this.location = location;
		this.description = description;
	}

	public EventDate getStart() {
		return this.start;
	}

	public void setStart(EventDate start) {
		this.start = start;
	}

	public EventDate getEnd() {
		return this.end;
	}

	public void setEnd(EventDate end) {
		this.end = end;
	}

	public String getSummary() {
		return this.summary;
	}

	public void setSummary(String summary) {
		this.summary = summary;
	}

	public String getLocation() {
		return this.location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getDescription() {
		return this.description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	static class EventDate {

		private String date;

		private String dateTime;

		@JsonCreator
		public EventDate(@JsonProperty("date") String date, @JsonProperty("dateTime")String dateTime) {
			this.date = date;
			this.dateTime = dateTime;
		}

		public String getDate() {
			return this.date;
		}

		public void setDate(String date) {
			this.date = date;
		}

		public String getDateTime() {
			return this.dateTime;
		}

		public void setDateTime(String dateTime) {
			this.dateTime = dateTime;
		}
	}
}
