package sagan;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashSet;

import com.google.gson.*;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.ClientConfig;

@Configuration
class SearchClientConfig {

    private static Log logger = LogFactory.getLog(SearchClientConfig.class);

    @Value("${elasticsearch.client.endpoint}")
    private String endpoint;

    @Bean
    public JestClient jestClient() {
        JestClientFactory factory = new JestClientFactory();
        factory.setClientConfig(clientConfig());
        return factory.getObject();
    }

    private ClientConfig clientConfig() {
        LinkedHashSet<String> servers = new LinkedHashSet<>();
        servers.add(endpoint);
        logger.info("**** Elastic Search endpoint: " + endpoint);
        return new ClientConfig.Builder(servers).gson(gson()).multiThreaded(true).build();
    }


    private Gson gson() {
        GsonBuilder gsonBuilder = new GsonBuilder();

        // TODO see issue #421
        gsonBuilder.registerTypeAdapter(LocalDateTime.class, new LocalDateTimeGsonTypeAdapter());

        return gsonBuilder.create();
    }

    /**
     * Gson type adapter for {@link java.time.LocalDateTime}.
     * <p>
     * <strong>NOTE: </strong> See issue #421. This class is currently implemented to
     * behave the same as for Gson and {@link java.util.Date}'s (that uses the
     * {@link java.text.DateFormat#MEDIUM} to serialize dates),
     * <p>
     * See comment below.
     */
    private static class LocalDateTimeGsonTypeAdapter implements JsonSerializer<LocalDateTime>,
            JsonDeserializer<LocalDateTime> {

        private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter
                .ofPattern("MMM d, yyyy hh:mm:ss a");

        // ref issue 421: DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssXXX")

        @Override
        public LocalDateTime deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context)
                throws JsonParseException {
            return LocalDateTime.parse(json.getAsString(), DATE_TIME_FORMATTER);
        }

        @Override
        public JsonElement serialize(LocalDateTime src, Type typeOfSrc, JsonSerializationContext context) {
            return new JsonPrimitive(DATE_TIME_FORMATTER.format(src));
        }
    }
}
