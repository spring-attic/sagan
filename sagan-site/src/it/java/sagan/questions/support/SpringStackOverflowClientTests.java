package sagan.questions.support;

import org.junit.Test;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import static org.junit.Assert.*;

public class SpringStackOverflowClientTests {

    @Test
    public void test() {
        RestTemplate restTemplate = new RestTemplate();
        StackOverflowClient client = new SpringStackOverflowClient(restTemplate);
        List<Question> questions = client.getQuestionsForTags("spring-framework");
        System.out.println("questions = " + questions);
    }

}