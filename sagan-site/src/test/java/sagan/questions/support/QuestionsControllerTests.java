package sagan.questions.support;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

/**
 * Unit tests for {@link QuestionsController}.
 */
public class QuestionsControllerTests {

    private QuestionsController questionsController;

    @Before
    public void setUp() throws Exception {
        questionsController = new QuestionsController();
    }

    @Test
    public void show() throws Exception {
        assertThat(questionsController.show(), equalTo("questions/show"));
    }
}
