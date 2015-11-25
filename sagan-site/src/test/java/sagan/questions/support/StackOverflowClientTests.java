package sagan.questions.support;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.web.client.RestTemplate;
import sagan.support.cache.CachedRestClient;

import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.contains;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;

/**
 * Tests for {@link StackOverflowClient}.
 */
public class StackOverflowClientTests {

    @Test
    public void getQuestionsTagged() {
        RestTemplate mockRestTemplate = mock(RestTemplate.class);
        Questions expected = new Questions();
        Question question = new Question();
        question.id = "12";
        expected.items = Collections.singletonList(question);
        given(mockRestTemplate.getForObject(anyString(), any())).willReturn(expected);

        StackOverflowClient client = new StackOverflowClient(new CachedRestClient(), mockRestTemplate);
        List<Question> actual = client.searchForQuestionsTagged("spring-data-mongodb", "spring-data-neo4j");
        Assert.assertThat(actual, contains(question));
        then(mockRestTemplate).should(times(1))
                .getForObject("https://api.stackexchange.com/2.2/search?filter=withbody&site=stackoverflow&tagged=spring-data-mongodb;spring-data-neo4j", Questions.class);

    }

}
