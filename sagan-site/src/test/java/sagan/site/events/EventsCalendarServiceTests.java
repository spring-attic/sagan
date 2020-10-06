package sagan.site.events;

import java.util.List;

import org.junit.jupiter.api.Test;
import sagan.site.SiteProperties;
import sagan.site.support.cache.CachedRestClient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Tests for {@link EventsCalendarService}
 */
@RestClientTest({EventsCalendarService.class, SiteProperties.class})
@TestPropertySource(properties = "sagan.site.events.calendar-uri=http://example.org/calendar")
public class EventsCalendarServiceTests {

	@Autowired
	private EventsCalendarService calendarService;

	@Autowired
	private MockRestServiceServer mockServer;

	@Test
	public void shouldFailWithoutCalendarUri() {
		EventsCalendarService service = new EventsCalendarService(new RestTemplateBuilder(), new CachedRestClient(), new SiteProperties());
		assertThatThrownBy(() -> service.findEvents(Period.of("2020-01-01", 10)))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("No calendar URI configured, see 'sagan.site.events.calendar-uri'");
	}

	@Test
	public void shouldFailWithMissingCalendar() {
		mockServer.expect(requestTo("http://example.org/calendar"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));
		assertThatThrownBy(() -> this.calendarService.findEvents(Period.of("2020-01-01", 10)))
				.isInstanceOf(InvalidCalendarException.class).hasMessage("calendar data not available");
	}

	@Test
	public void shouldFailForInvalidCalendar() {
		mockServer.expect(requestTo("http://example.org/calendar"))
				.andRespond(withSuccess(getClassPathResource("invalid.json"), MediaType.APPLICATION_JSON));
		assertThatThrownBy(() -> this.calendarService.findEvents(Period.of("2020-01-01", 20)))
				.isInstanceOf(InvalidCalendarException.class).hasMessage("could not parse Calendar data");
	}

	@Test
	public void shouldReturnSingleEvent() {
		mockServer.expect(requestTo("http://example.org/calendar"))
				.andRespond(withSuccess(getClassPathResource("single-event.json"), MediaType.APPLICATION_JSON));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-05-01", 30));
		assertThat(events).hasSize(1);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring IO conference");
		assertThat(event.formatStartTime()).isEqualTo("2020-05-14 - 09:00:00 +01:00");
		assertThat(event.formatEndTime()).isEqualTo("2020-05-16 - 18:00:00 +01:00");
		assertThat(event.getLocation()).isEqualTo("Barcelona, Spain");
		assertThat(event.getLink().toString()).isEqualTo("https://springio.net");
	}

	@Test
	public void shouldParseHtmlEvent() {
		mockServer.expect(requestTo("http://example.org/calendar"))
				.andRespond(withSuccess(getClassPathResource("html-event.json"), MediaType.APPLICATION_JSON));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-03-01", 30));
		assertThat(events).hasSize(1);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring Live");
		assertThat(event.formatStartTime()).isEqualTo("2020-03-19 - 09:00:00 -05:00");
		assertThat(event.formatEndTime()).isEqualTo("2020-03-21 - 18:00:00 -05:00");
		assertThat(event.getLocation()).isEqualTo("Virtual");
		assertThat(event.getLink().toString()).isEqualTo("https://events.example.org/event.html");
	}

	@Test
	public void shouldReturnManyEvents() {
		mockServer.expect(requestTo("http://example.org/calendar"))
				.andRespond(withSuccess(getClassPathResource("multi-events.json"), MediaType.APPLICATION_JSON));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-05-01", 30));
		assertThat(events).hasSize(2);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring IO conference");
		assertThat(event.formatStartTime()).isEqualTo("2020-05-14 - 09:00:00 +01:00");
		assertThat(event.formatEndTime()).isEqualTo("2020-05-16 - 18:00:00 +01:00");
		assertThat(event.getLocation()).isEqualTo("Barcelona, Spain");
		assertThat(event.getLink().toString()).isEqualTo("https://springio.net");
		event = events.get(1);
		assertThat(event.getSummary()).isEqualTo("Sample Event");
		assertThat(event.formatStartTime()).isEqualTo("2020-05-20 - 09:00:00 -08:00");
		assertThat(event.formatEndTime()).isEqualTo("2020-05-21 - 18:00:00 -08:00");
		assertThat(event.getLocation()).isEqualTo("Seattle, WA, USA");
		assertThat(event.getLink().toString()).isEqualTo("https://example.org/sample");
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, getClass());
	}

	@TestConfiguration
	static class CacheConfiguration {

		@Bean
		CachedRestClient cachedRestClient() {
			return new CachedRestClient();
		}
	}
}