package sagan.projects.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {
    @Mock
    private ProjectMetadataService projectMetadataService;

    private List<ProjectRelease> releases = new ArrayList<>();
    Project project = new Project("spring-framework", "spring", "http://example.com", "/project/spring-framework", releases,
            false, "project");
    private ExtendedModelMap model = new ExtendedModelMap();
    private ProjectController controller;
    private String viewName;

    @Before
    public void setUp() throws Exception {
        controller = new ProjectController(projectMetadataService);
        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        when(projectMetadataService.getProjectsForCategory("active")).thenReturn(Arrays.asList(project, project));
        viewName = controller.showProject(model, "spring-framework");
    }

    @Test
    public void showProjectModelHasProjectData() { assertThat(model.get("project"), equalTo(project)); }

    @Test
    public void showProjectModelHasProjectsListForSidebar() {
        List<Project> modelProjectList = (List<Project>) model.get("projects");
        assertThat(modelProjectList.size(), equalTo(2));
        assertThat(modelProjectList.get(0), equalTo(project));
    }

    @Test
    public void showProjectViewNameIsShow() {
        assertThat(viewName, is("projects/show"));
    }

}