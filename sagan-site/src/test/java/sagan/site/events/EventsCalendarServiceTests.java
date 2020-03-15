package sagan.site.events;

import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import sagan.SiteProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.test.web.client.response.MockRestResponseCreators;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;


/**
 * Tests for {@link EventsCalendarService}
 */
@RunWith(SpringRunner.class)
@RestClientTest({EventsCalendarService.class, SiteProperties.class})
@TestPropertySource(properties = "sagan.site.events.calendar-uri=http://example.org/ical")
public class EventsCalendarServiceTests {

	private static final MediaType TEXT_CALENDAR = MediaType.parseMediaType("text/calendar");

	@Autowired
	private EventsCalendarService calendarService;

	@Autowired
	private MockRestServiceServer mockServer;

	@Test
	public void shouldFailWithoutCalendarUri() {
		EventsCalendarService service = new EventsCalendarService(new RestTemplateBuilder(), new SiteProperties());
		assertThatThrownBy(() -> service.findEvents(Period.of("2020-01-01", 10)))
				.isInstanceOf(IllegalArgumentException.class).hasMessage("No calendar URI configured, see 'sagan.site.events.calendar-uri'");
	}

	@Test
	public void shouldFailWithMissingCalendar() {
		mockServer.expect(requestTo("http://example.org/ical"))
				.andRespond(MockRestResponseCreators.withStatus(HttpStatus.NOT_FOUND));
		assertThatThrownBy(() -> this.calendarService.findEvents(Period.of("2020-01-01", 10)))
				.isInstanceOf(InvalidCalendarException.class).hasMessage("calendar data not available");
	}

	@Test
	public void shouldFailForInvalidCalendar() {
		mockServer.expect(requestTo("http://example.org/ical"))
				.andRespond(withSuccess(getClassPathResource("invalid.ics"), TEXT_CALENDAR));
		assertThatThrownBy(() -> this.calendarService.findEvents(Period.of("2020-01-01", 20)))
				.isInstanceOf(InvalidCalendarException.class).hasMessage("could not parse iCal data");
	}

	@Test
	public void shouldReturnSingleEvent() {
		mockServer.expect(requestTo("http://example.org/ical"))
				.andRespond(withSuccess(getClassPathResource("single-event.ics"), TEXT_CALENDAR));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-05-01", 30));
		assertThat(events).hasSize(1);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring IO conference");
		assertThat(event.getFirstDay().toString()).isEqualTo("2020-05-14");
		assertThat(event.getLastDay().toString()).isEqualTo("2020-05-15");
		assertThat(event.getLocation()).isEqualTo("Barcelona, Spain");
		assertThat(event.getLink().toString()).isEqualTo("https://springio.net");
	}

	@Test
	public void shouldParseHtmlEvent() {
		mockServer.expect(requestTo("http://example.org/ical"))
				.andRespond(withSuccess(getClassPathResource("html-event.ics"), TEXT_CALENDAR));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-03-01", 30));
		assertThat(events).hasSize(1);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring Live");
		assertThat(event.getFirstDay().toString()).isEqualTo("2020-03-19");
		assertThat(event.getLastDay().toString()).isEqualTo("2020-03-20");
		assertThat(event.getLocation()).isEqualTo("Virtual");
		assertThat(event.getLink().toString()).isEqualTo("https://events.example.org/event.html");
	}

	@Test
	public void shouldReturnManyEvents() {
		mockServer.expect(requestTo("http://example.org/ical"))
				.andRespond(withSuccess(getClassPathResource("multi-events.ics"), TEXT_CALENDAR));
		List<Event> events = this.calendarService.findEvents(Period.of("2020-05-01", 30));
		assertThat(events).hasSize(2);
		Event event = events.get(0);
		assertThat(event.getSummary()).isEqualTo("Spring IO conference");
		assertThat(event.getFirstDay().toString()).isEqualTo("2020-05-14");
		assertThat(event.getLastDay().toString()).isEqualTo("2020-05-15");
		assertThat(event.isSingleDayEvent()).isFalse();
		assertThat(event.getLocation()).isEqualTo("Barcelona, Spain");
		assertThat(event.getLink().toString()).isEqualTo("https://springio.net");
		event = events.get(1);
		assertThat(event.getSummary()).isEqualTo("Sample Event");
		assertThat(event.getFirstDay().toString()).isEqualTo("2020-05-20");
		assertThat(event.getLastDay().toString()).isEqualTo("2020-05-20");
		assertThat(event.isSingleDayEvent()).isTrue();
		assertThat(event.getLocation()).isEqualTo("Seattle, WA, USA");
		assertThat(event.getLink().toString()).isEqualTo("https://example.org/sample");
	}

	private ClassPathResource getClassPathResource(String path) {
		return new ClassPathResource(path, getClass());
	}
}