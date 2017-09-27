package sagan.projects.support;

import sagan.blog.support.PostContentRenderer;
import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;
import sagan.projects.ProjectSample;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.mockito.Matchers.*;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class ProjectAdminControllerTests {

    @Mock
    private ProjectMetadataService projectMetadataService;

    @Mock
    private PostContentRenderer renderer;

    private List<ProjectRelease> releases = new ArrayList<>();
    Project project = new Project("spring-framework", "spring", "http://example.com", "http://examples.com", releases,
            "project");
    private ExtendedModelMap model = new ExtendedModelMap();
    private ProjectAdminController controller;

    @Before
    public void setUp() throws Exception {
        controller = new ProjectAdminController(this.projectMetadataService, this.renderer);
    }

    @Test
    public void listProjects_providesProjectMetadataServiceInModel() {
        releases.add(new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/1.2.3",
                "http://example.com/1.2.3", "org.springframework", "spring-core"));
        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        List<Project> list = Arrays.asList(project);
        when(projectMetadataService.getProjects()).thenReturn(list);
        controller.list(model);
        assertThat(model.get("projects"), equalTo(list));
        assertThat(project.getProjectReleases().iterator().next().getApiDocUrl(), equalTo(
                "http://example.com/1.2.3"));
    }

    @Test
    public void editProject_presentsVersionPatternsInUris() {
        releases.add(new ProjectRelease("1.2.3", ReleaseStatus.GENERAL_AVAILABILITY, false,
                "http://example.com/1.2.3",
                "http://example.com/1.2.3", "org.springframework", "spring-core"));
        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        controller.edit("spring-framework", model);
        assertThat(model.get("project"), equalTo(project));
        assertThat(((Project) model.get("project")).getProjectReleases().iterator().next().getApiDocUrl(), equalTo(
                "http://example.com/{version}"));
    }

    @Test
    public void saveProject_rendersAsciidocContent() {
        when(renderer.render(contains("boot-config"), any())).thenReturn("rendered-boot-config");
        when(renderer.render(contains("overview"), any())).thenReturn("rendered-overview");

        project.setRawBootConfig("boot-config");
        project.setRawOverview("overview");
        controller.save(project, null, null, "", null);

        ArgumentCaptor<Project> captor = ArgumentCaptor.forClass(Project.class);
        verify(projectMetadataService, times(1)).save(captor.capture());

        Project projectCaptured = captor.getValue();
        assertThat(projectCaptured.getRenderedBootConfig(), equalTo("rendered-boot-config"));
        assertThat(projectCaptured.getRenderedOverview(), equalTo("rendered-overview"));
    }

    @Test
    public void editProject_newProjectSampleDisplayOrder() {
        ProjectSample second = new ProjectSample("Second", 42);
        List<ProjectSample> samples = Arrays.asList(second);
        project.setProjectSamples(samples);
        when(projectMetadataService.getProject("spring-framework")).thenReturn(project);
        controller.edit("spring-framework", model);
        assertThat(model.get("projectSampleDisplayOrder"), equalTo(43));
    }
}
