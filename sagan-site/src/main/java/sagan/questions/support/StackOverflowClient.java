package sagan.questions.support;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

@Component
class StackOverflowClient {

    private static final String DEFAULT_BASE_URL = "https://api.stackexchange.com/2.2";
    private static final String SEARCH_TEMPLATE = "/search";

    private static final String TAGGED_PARAM = "tagged";

    private static final Map<String, String> PARAMETERS = new HashMap<>();

    static {
        PARAMETERS.put("filter", "withbody");
        PARAMETERS.put("site", "stackoverflow");
    }

    private final RestOperations restOperations;
    private String baseUrl;

    @Autowired
    public StackOverflowClient(RestOperations restOperations) {
        this.restOperations = restOperations;
        this.baseUrl = DEFAULT_BASE_URL;
    }

    /**
     * Search for questions that have one or more of the given tags.
     * See https://api.stackexchange.com/docs/search
     */
    public List<Question> searchForQuestionsTagged(String... tags) {
        UriComponentsBuilder builder = getBuilderFor(baseUrl + SEARCH_TEMPLATE);

        if (tags.length > 0) {
            String tagsString = StringUtils.arrayToDelimitedString(tags, ";");
            builder = builder.queryParam(TAGGED_PARAM, tagsString);
        }

        UriComponents uriComponents = builder.build();

        Questions result = restOperations.getForObject(uriComponents.toUri(), Questions.class);

        return result.items;
    }

    private UriComponentsBuilder getBuilderFor(String uri) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

        for (Entry<String, String> entry : PARAMETERS.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return builder;
    }
}
