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

import java.util.List;
import java.util.Optional;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Mockito.when;
import static sagan.projects.ProjectRelease.ReleaseStatus.GENERAL_AVAILABILITY;
import static sagan.projects.ProjectRelease.ReleaseStatus.SNAPSHOT;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsControllerTest {
    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private ProjectGuidesRepository projectGuidesRepo;

    @Mock
    private ProjectGuidesRepository projectTutorialRepo;

    @Mock
    private ProjectGuidesRepository projectTopicalRepo;

    private ProjectRelease currentRelease =
            new ProjectRelease("1.5.7.RELEASE", GENERAL_AVAILABILITY, true, "", "", "", "");
    private ProjectRelease anotherCurrentRelease =
            new ProjectRelease("1.4.7.RELEASE", GENERAL_AVAILABILITY, true, "", "", "", "");
    private ProjectRelease snapshotRelease = new ProjectRelease("1.7.7.SNAPSHOT", SNAPSHOT, false, "", "", "", "");
    private List<ProjectRelease> releases = asList(currentRelease, anotherCurrentRelease, snapshotRelease);
    Project project =
            new Project("spring-framework", "spring", "http://example.com", "/project/spring-framework", 0, releases,
					"project", "spring-cool,spring-awesome", "");
    Project projectUmbrella =
            new Project("spring-parapluie", "Spring Parapluie", "http://example.com", "/project/spring-parapluie",
                    1, releases, "project", "spring-cool,spring-awesome", "");
    Project projectUmbrellaChild =
            new Project("spring-parapluie-child", "Spring Parapluie Child", "http://example.com",
                    "/project/spring-parapluie-child", 1, releases, "project", "spring-cool,spring-awesome", "");

    private ExtendedModelMap model = new ExtendedModelMap();
    private ProjectsController controller;
    private String viewName;

    Topical topical = new Topical();
    Tutorial tutorial = new Tutorial();
    GettingStartedGuide guide = new GettingStartedGuide();

    @Before
    public void setUp() throws Exception {
        projectUmbrellaChild.setParentProject(projectUmbrella);
        projectUmbrella.setChildProjectList(asList(projectUmbrellaChild));

        when(projectTopicalRepo.findByProject(project)).thenReturn(asList(topical));
        when(projectTutorialRepo.findByProject(project)).thenReturn(asList(tutorial));
        when(projectGuidesRepo.findByProject(project)).thenReturn(asList(guide));

        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        when(projectMetadataService.getActiveTopLevelProjects())
                .thenReturn(asList(project, projectUmbrella));

        List<ProjectGuidesRepository> repositoryList =
                asList(projectGuidesRepo, projectTutorialRepo, projectTopicalRepo);

        controller = new ProjectsController(projectMetadataService, repositoryList);
        viewName = controller.showProject(model, "spring-framework");
    }

    @Test
    public void showProjectModelHasProjectData() {
        assertThat(model.get("selectedProject"), equalTo(project));
    }

    @Test
    public void showProjectModelHasProjectsListForSidebar() {
        List<Project> modelProjectList = (List<Project>) model.get("projects");
        assertThat(modelProjectList, hasSize(2));
        assertThat(modelProjectList, is(asList(project, projectUmbrella)));

        Project actualUmbrellaProject = modelProjectList.get(1);
        assertThat(actualUmbrellaProject.getChildProjectList(), equalTo(asList(projectUmbrellaChild)));
    }

    @Test
    public void showProjectViewNameIsShow() {
        assertThat(viewName, is("projects/show"));
    }

    @Test
    public void showProjectHasStackOverflowLink() {
        assertThat(model.get("projectStackOverflow"),
                is("https://stackoverflow.com/questions/tagged/spring-cool+or+spring-awesome"));
    }

    @Test
    public void showProjectHasGuidesTutorialsTopicals() {
        List<AbstractGuide> guides = (List<AbstractGuide>) model.get("guides");

        assertThat(guides, hasItems(guide, tutorial, topical));
    }

    @Test
    public void showProjectHasReleases() {
        Optional<ProjectRelease> currentRelease = (Optional<ProjectRelease>) model.get("currentRelease");
        List<ProjectRelease> otherReleases = (List<ProjectRelease>) model.get("otherReleases");

        assertThat(currentRelease, is(Optional.of(this.currentRelease)));
        assertThat(otherReleases, hasItems(anotherCurrentRelease, snapshotRelease));
    }

    @Test
    public void showProjectDoesNotExplodeWhenThereAreNoReleases() {
        Project projectWithoutReleases =
                new Project("spring-spline-reticulator", "spring-spline-reticulator", "http://example.com",
                        "/project/spring-spline-reticulator", 0, asList(),
                        "project", "spring-cool,spring-awesome", "");
        when(projectMetadataService.getProject("spring-spline-reticulator")).thenReturn(projectWithoutReleases);

        model = new ExtendedModelMap();
        controller.showProject(model, "spring-spline-reticulator");

        Optional<ProjectRelease> currentRelease = (Optional<ProjectRelease>) model.get("currentRelease");
        List<ProjectRelease> otherReleases = (List<ProjectRelease>) model.get("otherReleases");

        assertThat(currentRelease, is(Optional.empty()));
        assertThat(otherReleases, hasSize(0));
    }

	@Test
	public void listProjects_providesProjectMetadataServiceInModel() {
		controller.listProjects(model);
		assertThat((ProjectMetadataService) model.get("projectMetadata"), equalTo(projectMetadataService));
	}

	@Test
	public void listProjectReleases_providesReleaseMetadataInJsonPCallback() {
		controller.listProjects(model);
		assertThat((ProjectMetadataService) model.get("projectMetadata"), equalTo(projectMetadataService));
	}
}