package sagan.site.events;

import java.util.List;
import java.util.stream.Collectors;

import sagan.site.SiteProperties;
import sagan.site.support.cache.CachedRestClient;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@Service
public class EventsCalendarService {

	private final RestTemplate client;

	private final String calendarUri;

	private final CachedRestClient cachedClient;

	public EventsCalendarService(RestTemplateBuilder builder, CachedRestClient cachedClient, SiteProperties properties) {
		this.client = builder.build();
		this.cachedClient = cachedClient;
		this.calendarUri = properties.getEvents().getCalendarUri();
	}

	public List<Event> findEvents(Period period) {
		Assert.notNull(this.calendarUri, "No calendar URI configured, see 'sagan.site.events.calendar-uri'");
		try {
			GoogleCalendar googleCalendar = this.client.getForEntity(this.calendarUri, GoogleCalendar.class).getBody();
			return googleCalendar.getEvents().stream()
					.filter(period.toCalendarFilter())
					.sorted()
					.collect(Collectors.toList());
		}
		catch (HttpClientErrorException ex) {
			throw new InvalidCalendarException("calendar data not available", ex);
		}
		catch (Throwable throwable) {
			throw new InvalidCalendarException("could not parse Calendar data", throwable);
		}
	}

}
