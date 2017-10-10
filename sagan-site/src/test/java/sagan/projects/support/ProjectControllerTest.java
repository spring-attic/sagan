package sagan.projects.support;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;
import sagan.guides.AbstractGuide;
import sagan.guides.GettingStartedGuide;
import sagan.guides.Topical;
import sagan.guides.Tutorial;
import sagan.guides.support.ProjectGuidesRepository;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectControllerTest {
    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private ProjectGuidesRepository projectGuidesRepo;

    @Mock
    private ProjectGuidesRepository projectTutorialRepo;

    @Mock
    private ProjectGuidesRepository projectTopicalRepo;

    private List<ProjectRelease> releases = new ArrayList<>();
    Project project = new Project("spring-framework", "spring", "http://example.com", "/project/spring-framework", releases,
            false, "project", "spring-cool,spring-awesome", "");
    private ExtendedModelMap model = new ExtendedModelMap();
    private ProjectController controller;
    private String viewName;

    Topical topical = new Topical();
    Tutorial tutorial = new Tutorial();
    GettingStartedGuide guide = new GettingStartedGuide();

    @Before
    public void setUp() throws Exception {
        when(projectTopicalRepo.findByProject(project)).thenReturn(asList(topical));
        when(projectTutorialRepo.findByProject(project)).thenReturn(asList(tutorial));
        when(projectGuidesRepo.findByProject(project)).thenReturn(asList(guide));

        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        when(projectMetadataService.getProjectsForCategory("active")).thenReturn(asList(project, project));

        List<ProjectGuidesRepository> repositoryList =
                asList(projectGuidesRepo, projectTutorialRepo, projectTopicalRepo);

        controller = new ProjectController(projectMetadataService, repositoryList);
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

    @Test
    public void showProjectHasStackOverflowLink() { assertThat(model.get("projectStackOverflow"), is("https://stackoverflow.com/questions/tagged/spring-cool+or+spring-awesome"));}

    @Test
    public void showProjectHasGuidesTutorialsTopicals() {
        List<AbstractGuide> guides = (List<AbstractGuide>) model.get("guides");

        assertThat(guides, hasItems(guide, tutorial, topical));
    }
}