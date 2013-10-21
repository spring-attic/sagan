package sagan.projects.service;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.junit.Ignore;
import org.junit.Test;

import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.ResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.Assert.assertEquals;

public class ProjectMetadataServiceTests {

    @Test
    public void bindingToYaml() throws IOException {
        InputStream yaml = new ClassPathResource("/test-project-metadata.yml", getClass()).getInputStream();
        ProjectMetadataService metadataService = new ProjectMetadataYamlParser().createServiceFromYaml(yaml);

        assertEquals(3, metadataService.getProject("spring-framework").getProjectReleases().size());

        List<Project> activeProjects = metadataService.getProjectsForCategory("active");
        assertThat(activeProjects.get(0).getId(), is("spring-framework"));
    }

    @Test
    public void bindingToFullYaml() throws IOException {
        InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass()).getInputStream();
        ProjectMetadataService metadataService = new ProjectMetadataYamlParser().createServiceFromYaml(yaml);

        assertEquals(5, metadataService.getProject("spring-framework").getProjectReleases().size());
    }

    @Test
    @Ignore("Disabled as this test only needs to run on demand")
    public void linkTests() throws IOException {
        RestTemplate restTemplate = new RestTemplate();
        restTemplate.setErrorHandler(new ResponseErrorHandler() {

            @Override
            public boolean hasError(ClientHttpResponse response) throws IOException {
                return false;
            }

            @Override
            public void handleError(ClientHttpResponse response) throws IOException {
            }
        });
        InputStream yaml = new ClassPathResource("/project-metadata.yml", getClass()).getInputStream();
        ProjectMetadataService metadataService = new ProjectMetadataYamlParser().createServiceFromYaml(yaml);

        StringBuilder builder = new StringBuilder();
        for (Project project : metadataService.getProjects()) {
            for (ProjectRelease version : project.getProjectReleases()) {
                String apiDocUrl = version.getApiDocUrl();
                ResponseEntity<String> entity = restTemplate.getForEntity(apiDocUrl, String.class);
                if (entity.getStatusCode() != HttpStatus.OK) {
                    builder.append(project.getId() + ".invalid.apiUrl."
                            + version.getVersion() + ": " + apiDocUrl + "\n");
                }

                String refDocUrl = version.getRefDocUrl();
                entity = restTemplate.getForEntity(refDocUrl, String.class);
                if (entity.getStatusCode() != HttpStatus.OK) {
                    builder.append(project.getId() + ".invalid.referenceUrl."
                            + version.getVersion() + ": " + refDocUrl + "\n");
                }
            }
        }
        System.err.println(builder);
        assertEquals(3, metadataService.getProject("spring-framework").getProjectReleases().size());
    }
}
