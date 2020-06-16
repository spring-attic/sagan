package sagan.site.events;

import java.io.IOException;
import java.net.URI;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

public class EventDeserializer extends StdDeserializer<Event> {

	private static final Pattern URL_REGEXP = Pattern.compile("http[^\\s<\"]*");

	public EventDeserializer() {
		this(null);
	}

	public EventDeserializer(Class<?> vc) {
		super(vc);
	}

	@Override
	public Event deserialize(JsonParser jp, DeserializationContext ctxt)
			throws IOException, JsonProcessingException {
		JsonNode itemNode = jp.getCodec().readTree(jp);
		Event item = new Event();
		item.setStartTime(parseTime(itemNode.get("start")));
		item.setEndTime(parseTime(itemNode.get("end")));
		item.setSummary(itemNode.get("summary").textValue());
		item.setLocation(itemNode.get("location").textValue());
		URI link = parseLink(itemNode.get("description").textValue());
		item.setLink(link);
		return item;
	}

	private ZonedDateTime parseTime(JsonNode node) {
		JsonNode dateTime = node.get("dateTime");
		if (dateTime != null) {
			return ZonedDateTime.parse(dateTime.textValue());
		}
		else {
			return ZonedDateTime.of(LocalDate.parse(node.get("date").textValue()), LocalTime.MIDNIGHT, ZoneId.systemDefault());
		}
	}

	private URI parseLink(String description) {
		try {
			Matcher matcher = URL_REGEXP.matcher(description);
			String url = matcher.find() ? matcher.group() : "#";
			return URI.create(url);
		}
		catch (IllegalArgumentException ex) {
			return URI.create("#");
		}
	}

}
