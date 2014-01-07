package integration.admin;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.service.ProjectMetadataService;

import java.util.List;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;

import integration.AbstractIntegrationTests;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Rob Winch
 */
public class AdminProjectTests extends AbstractIntegrationTests {
    @Autowired
    private ProjectMetadataService projects;

    @Test
    public void projectsHomePage() throws Exception {
        mockMvc.perform(get("/admin/projects"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("projects"));
    }

    @Test
    public void viewProject() throws Exception {
        mockMvc.perform(get("/admin/projects/spring-framework"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("project"));
    }

    @Test
    public void viewProject404() throws Exception {
        mockMvc.perform(get("/admin/projects/missing"))
                .andExpect(view().name("pages/404"));
    }

    @Test
    public void deleteProject() throws Exception {
        mockMvc.perform(delete("/admin/projects/spring-framework"))
                .andExpect(redirectedUrl("./?navSection=projects"));

        assertThat(projects.getProject("spring-framework"),equalTo(null));
    }

    @Test
    public void newProject() throws Exception {
        mockMvc.perform(get("/admin/projects/new"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("project"));
    }

    @Test
    public void saveProjectRemoveRelease() throws Exception {
        MockHttpServletRequestBuilder request
            = post("/admin/projects/spring-framework")
                .param("groupId", "org.springframework")
                .param("id", "spring-framework")
                .param("category", "active")
                .param("name", "Spring Framework")
                .param("repoUrl", "http://github.com/spring-projects/spring-framework")
                .param("siteUrl", "http://projects.spring.io/spring-framework")
                .param("releasesToDelete", "4.0.1.BUILD-SNAPSHOT")
                .param("projectReleases[0].version", "4.0.1.BUILD-SNAPSHOT")
                .param("projectReleases[0].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[0].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[1].version", "4.0.0.RELEASE")
                .param("projectReleases[1].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[1].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[2].version", "3.2.7.BUILD-SNAPSHOT")
                .param("projectReleases[2].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[2].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[3].version", "3.2.6.RELEASE")
                .param("projectReleases[3].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[3].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[4].version", "3.1.4.RELEASE")
                .param("projectReleases[4].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[4].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/");

        mockMvc.perform(request)
                .andExpect(redirectedUrl("spring-framework?navSection=projects"));

        assertThat(projects.getProject("spring-framework").getProjectReleases().get(0).getVersion(), equalTo("4.0.0.RELEASE"));
    }

    @Test
    public void saveProjectUpdateVersion() throws Exception {
        MockHttpServletRequestBuilder request
                = post("/admin/projects/spring-framework")
                .param("groupId", "org.springframework")
                .param("id", "spring-framework")
                .param("category", "active")
                .param("name", "Spring Framework")
                .param("repoUrl", "http://github.com/spring-projects/spring-framework")
                .param("siteUrl", "http://projects.spring.io/spring-framework")
                .param("projectReleases[0].version", "4.0.2.BUILD-SNAPSHOT")
                .param("projectReleases[0].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[0].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[1].version", "4.0.1.RELEASE")
                .param("projectReleases[1].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[1].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[2].version", "3.2.7.BUILD-SNAPSHOT")
                .param("projectReleases[2].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[2].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[3].version", "3.2.6.RELEASE")
                .param("projectReleases[3].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[3].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[4].version", "3.1.4.RELEASE")
                .param("projectReleases[4].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-framework-reference/htmlsingle/")
                .param("projectReleases[4].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/");

        mockMvc.perform(request)
                .andExpect(redirectedUrl("spring-framework?navSection=projects"));


        List<ProjectRelease> projectReleases = projects.getProject("spring-framework").getProjectReleases();
        assertThat(projectReleases.get(0).getVersion(), equalTo("4.0.2.BUILD-SNAPSHOT"));
        assertThat(projectReleases.get(1).getVersion(), equalTo("4.0.1.RELEASE"));
    }

    @Test
    public void saveProjectNewProject() throws Exception {
        MockHttpServletRequestBuilder request
                = post("/admin/projects/spring-new")
                .param("groupId", "org.springframework")
                .param("id", "spring-new")
                .param("category", "active")
                .param("name", "Spring New")
                .param("repoUrl", "http://github.com/spring-projects/spring-new")
                .param("siteUrl", "http://projects.spring.io/spring-new")
                .param("projectReleases[0].version", "4.0.2.BUILD-SNAPSHOT")
                .param("projectReleases[0].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-new-reference/htmlsingle/")
                .param("projectReleases[0].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[1].version", "4.0.1.RELEASE")
                .param("projectReleases[1].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-new-reference/htmlsingle/")
                .param("projectReleases[1].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[2].version", "3.2.7.BUILD-SNAPSHOT")
                .param("projectReleases[2].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-new-reference/htmlsingle/")
                .param("projectReleases[2].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[3].version", "3.2.6.RELEASE")
                .param("projectReleases[3].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-new-reference/htmlsingle/")
                .param("projectReleases[3].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/")

                .param("projectReleases[4].version", "3.1.4.RELEASE")
                .param("projectReleases[4].refDocUrl", "http://docs.spring.io/spring/docs/{version}/spring-new-reference/htmlsingle/")
                .param("projectReleases[4].apiDocUrl", "http://docs.spring.io/spring/docs/{version}/javadoc-api/");

        mockMvc.perform(request)
                .andExpect(redirectedUrl("spring-new?navSection=projects"));

        Project project = projects.getProject("spring-new");
        assertThat(project.getName(),equalTo("Spring New"));
        List<ProjectRelease> projectReleases = project.getProjectReleases();
        assertThat(projectReleases.get(0).getVersion(), equalTo("4.0.2.BUILD-SNAPSHOT"));
        assertThat(projectReleases.get(1).getVersion(), equalTo("4.0.1.RELEASE"));
    }
}
