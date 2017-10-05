package sagan.questions.support;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.ui.ExtendedModelMap;
import sagan.projects.Project;
import sagan.projects.support.ProjectMetadataService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.BDDMockito.given;

/**
 * Unit tests for {@link QuestionsController}.
 */
public class QuestionsControllerTests {

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private StackOverflowClient stackOverflow;

    private QuestionsController questionsController;

    private ExtendedModelMap model;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        model = new ExtendedModelMap();

        Question q1 = new Question();
        q1.title = "Question 1";

        Question q2 = new Question();
        q2.title = "Question 2";

        given(stackOverflow.searchForQuestionsTagged("spring")).willReturn(Arrays.asList(q1, q2));

        given(projectMetadataService.getProjects()).willReturn(Arrays.asList(
                new Project("spring-framework", "Spring Framework", null, null,
                        Collections.emptyList(), false, "active", "spring-core, spring-framework, spring", ""),
                new Project("spring-data", "Spring Data", null, null,
                        Collections.emptyList(), true, "active", "spring-data,spring-data-mongodb", ""),
                new Project("spring-data-mongodb", "Spring Data MongoDB", null, null, Collections.emptyList(), false, "active", "", ""),
                new Project("spring-data-graph", "Spring Data Graph", null, null, Collections.emptyList(), false, "attic", "", ""),
                new Project("spring-scala", "Spring Scala", null, null, Collections.emptyList(), false, "incubator", "", "")
        ));
        questionsController = new QuestionsController(projectMetadataService, stackOverflow);
    }

    @Test
    public void index() throws Exception {

        assertThat(questionsController.show(model), equalTo("questions/index"));

        assertThat(((List<Question>) model.get("questions")).stream()
                        .map(question -> question.title)
                        .collect(Collectors.toList()),
                contains("Question 1", "Question 2")
        );

        assertThat(((List<Project>) model.get("projects")).stream()
                        .map(Project::getName)
                        .collect(Collectors.toList()),
                contains("Spring Data", "Spring Framework")
        );
    }
}
