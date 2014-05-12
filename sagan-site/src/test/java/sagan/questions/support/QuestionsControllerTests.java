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

    private QuestionsController questionsController;

    private ExtendedModelMap model;

    @Before
    public void setUp() throws Exception {

        MockitoAnnotations.initMocks(this);
        model = new ExtendedModelMap();

        given(projectMetadataService.getProjects()).willReturn(Arrays.asList(
                new Project("spring-framework", "Spring Framework", null, null,
                        Collections.emptyList(), false, "active", "spring-core, spring-framework, spring"),
                new Project("spring-data", "Spring Data", null, null,
                        Collections.emptyList(), true, "active", "spring-data,spring-data-mongodb"),
                new Project("spring-data-mongodb", "Spring Data MongoDB", null, null, Collections.emptyList(), false, "active", ""),
                new Project("spring-data-graph", "Spring Data Graph", null, null, Collections.emptyList(), false, "attic", ""),
                new Project("spring-scala", "Spring Scala", null, null, Collections.emptyList(), false, "incubator", "")
        ));
        questionsController = new QuestionsController(projectMetadataService);
    }

    @Test
    public void index() throws Exception {

        assertThat(questionsController.show(model), equalTo("questions/index"));
        assertThat(((List<Project>) model.get("projects")).stream()
                        .map(project -> project.getName())
                        .collect(Collectors.toList()),
                contains("Spring Data", "Spring Framework")
        );
    }
}
