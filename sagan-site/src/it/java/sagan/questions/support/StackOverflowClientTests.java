package sagan.questions.support;

import java.util.List;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;

import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Integration tests for {@link StackOverflowClient}.
 */
public class StackOverflowClientTests {

    @Test
    public void test() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        StackOverflowClient client = new StackOverflowClient(restTemplate);
        List<Question> questions = client.getQuestionsForTags("spring");
        System.out.println("questions = " + questions);
    }

}
