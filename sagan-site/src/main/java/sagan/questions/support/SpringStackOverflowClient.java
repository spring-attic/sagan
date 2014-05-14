package sagan.questions.support;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestOperations;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import static sagan.questions.support.SpringStackOverflowClient.StackOverflowResult.Answers;
import static sagan.questions.support.SpringStackOverflowClient.StackOverflowResult.Questions;

@Component
class SpringStackOverflowClient implements StackOverflowClient {

    private static final String DEFAULT_BASE_URL = "https://api.stackexchange.com/2.2";
    private static final String QUESTIONS_TEMPLATE = "/questions";
    private static final String ANSWERS_TEMPLATE = QUESTIONS_TEMPLATE + "/%s/answers";

    private static final String TAGGED_PARAM = "tagged";

    private static final Map<String, String> PARAMETERS = new HashMap<>();

    static {
        PARAMETERS.put("filter", "withbody");
        PARAMETERS.put("site", "stackoverflow");
    }

    private final RestOperations restOperations;
    private String baseUrl;

    /**
     * Creates a new {@link SpringStackOverflowClient} using the given {@link RestOperations}.
     *
     * @param restOperations
     */
    @Autowired
    public SpringStackOverflowClient(RestOperations restOperations) {

        this.restOperations = restOperations;
        this.baseUrl = DEFAULT_BASE_URL;
    }

    public List<Question> getQuestionsForTags(String... tags) {

        UriComponentsBuilder builder = getBuilderFor(baseUrl + QUESTIONS_TEMPLATE);

        if (tags.length > 0) {
            String tagsString = StringUtils.arrayToDelimitedString(tags, ";");
            builder = builder.queryParam(TAGGED_PARAM, tagsString);
        }

        UriComponents uriComponents = builder.build();

        System.out.println("builder = " + builder.build().toUri());

        Questions result = restOperations.getForObject(uriComponents.toUri(), Questions.class);

        return result.items;
    }

    @Override
    public Answer getAcceptedAnswerFor(Question question) {

        Assert.isTrue(question.isAnswered, "The question does not have an answer!");

        UriComponentsBuilder builder = getBuilderFor(baseUrl + String.format(ANSWERS_TEMPLATE, question.id));

        Answers answers = restOperations.getForObject(builder.build().toUri(), Answers.class);
        return answers.getAccepted();
    }

    private UriComponentsBuilder getBuilderFor(String uri) {

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(uri);

        for (Entry<String, String> entry : PARAMETERS.entrySet()) {
            builder.queryParam(entry.getKey(), entry.getValue());
        }

        return builder;
    }

    static class StackOverflowResult<T> {

        @JsonProperty
        protected List<T> items;

        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Questions extends StackOverflowResult<Question> {
        }

        static class Answers extends StackOverflowResult<Answer> {

            public Answer getAccepted() {

                for (Answer answer : items) {
                    if (answer.accepted) {
                        return answer;
                    }
                }

                return null;
            }
        }
    }
}