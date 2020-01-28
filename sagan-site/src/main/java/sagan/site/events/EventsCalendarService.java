package sagan.site.events;

import java.net.URI;
import java.util.List;
import java.util.TimeZone;
import java.util.stream.Collectors;

import biweekly.Biweekly;
import biweekly.ICalendar;
import biweekly.component.VEvent;
import sagan.SiteProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EventsCalendarService {

	private final RestTemplate client;

	private final URI calendarUri;

	public EventsCalendarService(RestTemplateBuilder builder, SiteProperties properties) {
		this.client = builder.build();
		this.calendarUri = properties.getEvents().getCalendarUri();
	}

	public List<Event> findEvents(Period period) {
		Assert.notNull(this.calendarUri, "No calendar URI configured, see 'sagan.site.events.calendar-uri'");
		try {
			String rawCalendar = this.client.getForEntity(this.calendarUri.toString(), String.class).getBody();
			ICalendar iCalendar = Biweekly.parse(rawCalendar).first();
			TimeZone calendarTimeZone = getCalendarTimeZone(iCalendar);
			List<VEvent> events = iCalendar.getEvents();
			return events.stream()
					.filter(period.toCalendarFilter())
					.map(event -> new Event(event, calendarTimeZone))
					.collect(Collectors.toList());
		}
		catch (HttpClientErrorException ex) {
			throw new InvalidCalendarException("calendar data not available", ex);
		}
		catch (Throwable throwable) {
			throw new InvalidCalendarException("could not parse iCal data", throwable);
		}
	}

	private TimeZone getCalendarTimeZone(ICalendar iCalendar) {
		if (iCalendar.getTimezoneInfo().getDefaultTimezone() != null) {
			return iCalendar.getTimezoneInfo().getDefaultTimezone().getTimeZone();
		}
		if (iCalendar.getExperimentalProperties("X-WR-TIMEZONE").size() > 0) {
			return TimeZone.getTimeZone(iCalendar.getExperimentalProperties("X-WR-TIMEZONE").get(0).getValue());
		}
		return TimeZone.getDefault();
	}
}
