package sagan.questions.support;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Test;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class SpringStackOverflowClientTests {

    @Test
    public void test() {
        HttpClient httpClient = HttpClientBuilder.create().build();
        ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);

        RestTemplate restTemplate = new RestTemplate(requestFactory);
        StackOverflowClient client = new SpringStackOverflowClient(restTemplate);
        List<Question> questions = client.getQuestionsForTags("spring");
        System.out.println("questions = " + questions);
    }

}