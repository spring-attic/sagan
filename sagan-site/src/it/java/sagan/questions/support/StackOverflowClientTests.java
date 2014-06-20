package sagan.questions.support;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import sagan.SiteConfig;
import saganx.AbstractIntegrationTests;

import static org.hamcrest.Matchers.anyOf;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasItems;

/**
 * Integration tests for {@link StackOverflowClient}.
 */
public class StackOverflowClientTests extends AbstractIntegrationTests {

    @Autowired
    private SiteConfig siteConfig;

    @Test
    public void getQuestionsTagged() {
        StackOverflowClient client = new StackOverflowClient(siteConfig.restTemplate());
        List<Question> questions = client.searchForQuestionsTagged("spring-data-mongodb", "spring-data-neo4j");
        Assert.assertThat(questions.get(0).tags, anyOf(hasItem("spring-data-mongodb"), hasItem("spring-data-neo4j")));
    }

}
