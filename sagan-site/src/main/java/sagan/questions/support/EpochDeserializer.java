package sagan.questions.support;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;

/**
 * Jackson deserializer for JDK8's {@link java.time.LocalDateTime}.
 * @author Brian Clozel
 * @see <a href="https://api.stackexchange.com/docs/dates">StackExchange API Dates format</a>
 */
public class EpochDeserializer extends JsonDeserializer<LocalDateTime> {

	@Override
	public LocalDateTime deserialize(JsonParser jp, DeserializationContext ctxt) throws IOException {
		Long timestamp = jp.getLongValue();
		return LocalDateTime.ofEpochSecond(timestamp, 0, ZoneOffset.UTC);
	}
}
