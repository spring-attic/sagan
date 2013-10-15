package sagan.projects.web;

import sagan.projects.service.ProjectMetadataService;
import sagan.projects.web.ProjectsController;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.springframework.ui.ExtendedModelMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@RunWith(MockitoJUnitRunner.class)
public class ProjectsControllerTests {

    @Mock
    private ProjectMetadataService projectMetadataService;

    private ExtendedModelMap model = new ExtendedModelMap();
    private ProjectsController controller;

    @Before
    public void setUp() throws Exception {
        this.controller = new ProjectsController(this.projectMetadataService);
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
