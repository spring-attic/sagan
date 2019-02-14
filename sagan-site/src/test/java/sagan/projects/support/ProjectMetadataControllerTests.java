package sagan.projects.support;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;
import sagan.projects.Project;
import sagan.projects.ProjectPatchingService;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ProjectMetadataControllerTests {

    /**
     * 
     */
    private static final String PROJECT_ID = "spring-framework";

    @Mock
    private ProjectMetadataService projectMetadataService;

    private List<ProjectRelease> releases = new ArrayList<>();
    Project project = new Project(PROJECT_ID, "spring", "http://example.com", "http://examples.com", releases,
            "project");
    private ProjectMetadataController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ProjectMetadataController(projectMetadataService, new ProjectPatchingService());
    }

    @Test
    public void getProject_doesNotContainVersionPlaceholders() throws Exception {
        ProjectRelease release = new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/1.2.3",
                "http://example.com/1.2.3", "org.springframework", "spring-core");
        releases.add(release);
        when(projectMetadataService.getProject(PROJECT_ID)).thenReturn(project);
        Project result = controller.projectMetadata(PROJECT_ID);
        assertThat(result.getProjectRelease("1.2.3"), equalTo(release));
        assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), equalTo(
                "http://example.com/1.2.3"));
    }

    @Test
    public void editProjectReleases_replacesVersionPatterns() throws Exception {
        ProjectRelease release = new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/1.2.3",
                "http://example.com/1.2.3", "org.springframework", "spring-core");
        ProjectRelease update = new ProjectRelease("1.2.4", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/{version}",
                "http://example.com/{version}", "org.springframework", "spring-core");
        releases.add(release);
        when(projectMetadataService.getProject(PROJECT_ID)).thenReturn(project);
        controller.updateProjectMetadata(PROJECT_ID, Arrays.asList(update));
        assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), equalTo(
                "http://example.com/1.2.4"));
    }

    @Test
    public void addProjectRelease_replacesVersionPatterns() throws Exception {
        ProjectRelease update = new ProjectRelease("1.2.4", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/{version}",
                "http://example.com/{version}", "org.springframework", "spring-core");
        when(projectMetadataService.getProject(PROJECT_ID)).thenReturn(project);
        controller.updateReleaseMetadata(PROJECT_ID, update);
        assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), equalTo(
                "http://example.com/1.2.4"));
    }

    @Test
    public void updateProject_patchesTheProject() throws Exception {
        List<ProjectRelease> newReleases = new ArrayList<>();
        newReleases.add(new ProjectRelease("1.0.0.RELEASE", ReleaseStatus.GENERAL_AVAILABILITY, true, "foo", "bar", "com.example", "artifact"));
        Project newProject = new Project(PROJECT_ID, null, "http://example.com", "newSite", newReleases, "newProject");
        newProject.setRawBootConfig("newRawBootConfig");
        newProject.setRawOverview("newRawOverview");
        when(projectMetadataService.getProject(PROJECT_ID)).thenReturn(project);
        when(projectMetadataService.save(Mockito.anyObject()))
                .thenAnswer(invocation -> invocation.getArguments()[0]);

        Project updatedProject = controller.updateProject(PROJECT_ID, newProject);

        // for now we only patch the raw stuff
        assertThat(updatedProject.getName(), equalTo(project.getName()));
        assertThat(updatedProject.getRepoUrl(), equalTo(project.getRepoUrl()));

        assertThat(updatedProject.getRawOverview(), equalTo("newRawOverview"));
        assertThat(updatedProject.getRawBootConfig(), equalTo("newRawBootConfig"));
    }

}
