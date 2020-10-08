package sagan.site.events;

import java.util.List;
import java.util.stream.Collectors;

import sagan.site.SiteProperties;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EventsCalendarService {

	public static final String CACHE_EVENTS = "cache.events";

	private final RestTemplate client;

	private final String calendarUri;

	public EventsCalendarService(RestTemplateBuilder builder, SiteProperties properties) {
		this.client = builder.build();
		this.calendarUri = properties.getEvents().getCalendarUri();
	}

	public List<Event> findEvents(Period period) {
		return fetchGoogleCalendar().getEvents().stream()
				.map(Event::new)
				.filter(period.toCalendarFilter())
				.sorted()
				.collect(Collectors.toList());
	}

	@Cacheable(CACHE_EVENTS)
	public GoogleCalendar fetchGoogleCalendar() {
		Assert.notNull(this.calendarUri, "No calendar URI configured, see 'sagan.site.events.calendar-uri'");
		try {
			return this.client.getForEntity(this.calendarUri, GoogleCalendar.class).getBody();
		}
		catch (HttpClientErrorException ex) {
			throw new InvalidCalendarException("calendar data not available", ex);
		}
		catch (Throwable throwable) {
			throw new InvalidCalendarException("could not parse Calendar data", throwable);
		}
	}

}
