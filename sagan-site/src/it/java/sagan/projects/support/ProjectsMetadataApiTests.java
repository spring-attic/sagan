package sagan.projects.support;

import sagan.projects.Project;
import sagan.projects.ProjectRelease;
import sagan.projects.ProjectRelease.ReleaseStatus;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.persistence.EntityManager;

import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.json.JacksonJsonParser;
import org.springframework.boot.test.autoconfigure.restdocs.AutoConfigureRestDocs;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultHandler;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.client.WireMock;

import static junit.framework.TestCase.fail;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.*;
import static org.springframework.cloud.contract.wiremock.restdocs.WireMockRestDocs.verify;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import saganx.AbstractIntegrationTests;

@AutoConfigureRestDocs(outputDir = "build/snippets")
public class ProjectsMetadataApiTests extends AbstractIntegrationTests {

    @Autowired
    private ProjectMetadataService service;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private EntityManager entityManager;

    private ResultHandler docs(String name) {
        return document(name,
                preprocessRequest(prettyPrint()),
                preprocessResponse(prettyPrint()));
    }

    @Test
    public void projectMetadata_respondsWithData() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/project_metadata/spring-framework"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andDo(verify().wiremock(
                        WireMock.get(WireMock.urlPathMatching("/project_metadata/spring-framework")).atPriority(1)
                        ).stub("project"));
    }

    @Test
    public void projectMetadata_respondsWithJavascript() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/project_metadata/spring-framework?callback=a_function_name"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/javascript"))
                .andDo(verify().wiremock(WireMock.get(WireMock.urlPathMatching("/project_metadata/spring-framework"))
                        .withQueryParam("callback", WireMock.matching("a_function_name")).atPriority(5)
                        ).stub("callback"));
    }

    @Test
    public void projectMetadata_respondsWithCallback_andData() throws Exception {
        List<Object> releases = getAndCheckProjectReleases("spring-framework", "Spring Framework");

        checkCurrentRelease(releases);
        checkMilestone(releases);
    }

    @Test
    public void projectMetadata_withPreRelease() throws Exception {
        List<Object> releases = getAndCheckProjectReleases("spring-framework", "Spring Framework");

        checkMilestone(releases);
    }

    @Test
    public void projectMetadata_addRelease() throws Exception {
        ProjectRelease release = new ProjectRelease("1.2.3.BUILD-SNAPSHOT", ReleaseStatus.SNAPSHOT, false,
                "http://example.com/{version}", "http://example.com/api/{version}", "org.springframework",
                "spring-core");
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/project_metadata/spring-framework/releases").content(mapper.writeValueAsString(
                                        getRelease(release)))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(containsString("http://example.com/1.2.3.BUILD-SNAPSHOT")))
                .andDo(docs("add_release"));
    }

    @Test
    public void projectMetadata_updateRelease() throws Exception {
        Project project = service.getProject("spring-framework");
        ProjectRelease release = project.getProjectReleases().iterator().next().createWithVersionPattern();
        release.setVersion("1.2.3.RELEASE");
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .post("/project_metadata/spring-framework/releases").content(mapper.writeValueAsString(
                                        getRelease(release)))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andExpect(content().string(containsString("/spring/docs/1.2.3.RELEASE")))
                .andExpect(content().string(not(containsString("null"))))
                .andDo(docs("add_release"));
    }

    @Test
    public void projectMetadata_getRelease() throws Exception {
        Project project = service.getProject("spring-framework");
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/project_metadata/spring-framework/releases/" + project.getProjectReleases()
                                        .iterator()
                                        .next().getVersion()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andDo(docs("get_release"));
    }

    @Test
    public void projectMetadata_updateProject() throws Exception {
        Project project = service.getProject("spring-framework");
        List<Map<String, Object>> releases = new ArrayList<>();
        for (ProjectRelease release : project.getProjectReleases()) {
            release = release.createWithVersionPattern();
            Map<String, Object> map = getRelease(release);
            releases.add(map);
        }
        releases.iterator().next().put("version", "1.2.8.RELEASE");
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .put("/project_metadata/spring-framework/releases").content(mapper.writeValueAsString(
                                        releases))
                                .contentType(MediaType.APPLICATION_JSON_UTF8))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andDo(docs("update_project"));
        entityManager.flush();
    }

    private Map<String, Object> getRelease(ProjectRelease release) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("groupId", release.getGroupId());
        map.put("artifactId", release.getArtifactId());
        map.put("version", release.getVersion());
        map.put("releaseStatus", release.getReleaseStatus());
        if (release.isCurrent()) {
            map.put("current", true);
        }
        map.put("refDocUrl", release.getRefDocUrl());
        map.put("apiDocUrl", release.getApiDocUrl());
        if (release.getRepository() != null) {
            map.put("repository", release.getRepository());
        }

        return map;
    }

    @Test
    public void projectMetadata_getMissingRelease() throws Exception {
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .get("/project_metadata/spring-framework/releases/FOO"))
                .andExpect(status().isNotFound());
    }

    @Test
    public void projectMetadata_deleteRelease() throws Exception {
        Project project = service.getProject("spring-framework");
        mockMvc
                .perform(
                        MockMvcRequestBuilders
                                .delete("/project_metadata/spring-framework/releases/" + project.getProjectReleases()
                                        .iterator()
                                        .next().getVersion()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("application/json"))
                .andDo(docs("delete_release"));
    }

    public List<Object> getAndCheckProjectReleases(String projectId, String expectedProjectName) throws Exception {
        MvcResult result = mockMvc.perform(
                MockMvcRequestBuilders.get("/project_metadata/" + projectId
                        + "?callback=a_function_name")).andReturn();

        String content = result.getResponse().getContentAsString();

        String functionNameRegex = "^([^(]*)\\((.*)\\);$";
        Matcher matcher = Pattern.compile(functionNameRegex).matcher(content);
        if (matcher.find()) {
            assertThat(matcher.group(1), equalTo("/**/a_function_name"));

            Map<String, Object> projectMetadata = new JacksonJsonParser().parseMap(matcher.group(2));

            assertThat((String) projectMetadata.get("name"), equalTo(expectedProjectName));
            @SuppressWarnings("unchecked")
            List<Object> list = (List<Object>) projectMetadata.get("projectReleases");
            return list;
        } else {
            fail(String.format("no match found: %s", content));
            return null;
        }
    }

    private void checkCurrentRelease(List<Object> releases) {

        @SuppressWarnings("unchecked")
        Map<String, Object> release = (Map<String, Object>) releases.stream()
				.filter(r -> (Boolean) ((Map<String, Object>)r).get("current"))
				.findFirst().get();
        assertThat((String) release.get("version"), endsWith(".RELEASE"));
        assertThat(
                (String) release.get("refDocUrl"),
                containsString("/spring-framework-reference/"));
        assertThat((String) release.get("apiDocUrl"),
                containsString("/javadoc-api/"));
        assertThat((Boolean) release.get("preRelease"), equalTo(false));
        assertThat((Boolean) release.get("generalAvailability"), equalTo(true));
        assertThat((Boolean) release.get("snapshot"), equalTo(false));

        Map<String, Object> repository = getRepository(release);
        assertThat(repository, nullValue());
    }

    private void checkMilestone(List<Object> releases) {
        @SuppressWarnings("unchecked")
		Map<String, Object> release = (Map<String, Object>) releases.stream()
				.filter(r -> (Boolean) ((Map<String, Object>)r).get("preRelease"))
				.findFirst().get();
        assertThat((String) release.get("version"), containsString(".RC"));
        assertThat((String) release.get("refDocUrl"),
                containsString("/spring-framework-reference/"));
        assertThat((String) release.get("apiDocUrl"),
                containsString("/javadoc-api/"));
        assertThat((Boolean) release.get("preRelease"), equalTo(true));
        assertThat((Boolean) release.get("current"), equalTo(false));
        assertThat((Boolean) release.get("generalAvailability"), equalTo(false));
        assertThat((Boolean) release.get("snapshot"), equalTo(false));

        Map<String, Object> repository = getRepository(release);

        assertThat((String) repository.get("id"), equalTo("spring-milestones"));
        assertThat((String) repository.get("name"), equalTo("Spring Milestones"));
        assertThat((String) repository.get("url"), equalTo("https://repo.spring.io/libs-milestone"));
        assertThat((Boolean) repository.get("snapshotsEnabled"), equalTo(false));
    }

    private Map<String, Object> getRepository(Map<String, Object> release) {
        @SuppressWarnings("unchecked")
        Map<String, Object> map = (Map<String, Object>) release.get("repository");
        return map;
    }

}
