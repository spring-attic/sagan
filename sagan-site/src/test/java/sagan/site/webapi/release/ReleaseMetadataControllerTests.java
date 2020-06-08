package sagan.site.webapi.release;

import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.modelmapper.ModelMapper;
import sagan.site.projects.Project;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.Release;
import sagan.site.projects.SupportStatus;
import sagan.site.projects.Version;
import sagan.site.webapi.ConstrainedFields;
import sagan.site.webapi.WebApiTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.MediaType;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.verify;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.requestFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static sagan.site.webapi.ConstrainedFields.constraintsOn;

/**
 * Tests for {@link ReleaseMetadataController}
 */
@RunWith(SpringRunner.class)
@WebApiTest(ReleaseMetadataController.class)
public class ReleaseMetadataControllerTests {

	private static final ObjectMapper objectMapper = new ObjectMapper();

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectMetadataService metadataService;

	private Project springBoot;

	private Release currentRelease;

	@Before
	public void setup() {
		this.springBoot = new Project("spring-boot", "Spring Boot");
		this.springBoot.setRepoUrl("https://github.com/spring-projects/spring-boot");
		this.springBoot.setStatus(SupportStatus.ACTIVE);
		this.currentRelease = this.springBoot.addRelease(Version.of("2.3.0.RELEASE"));
		this.currentRelease.setCurrent(true);
		this.currentRelease.setApiDocUrl("https://docs.spring.io/spring-boot/docs/{version}/api/");
		this.currentRelease.setRefDocUrl("https://docs.spring.io/spring-boot/docs/{version}/reference/html/");
		Release twoThreeOneSnap = this.springBoot.addRelease(Version.of("2.3.1.BUILD-SNAPSHOT"));
		twoThreeOneSnap.setApiDocUrl("https://docs.spring.io/spring-boot/docs/{version}/api/");
		twoThreeOneSnap.setRefDocUrl("https://docs.spring.io/spring-boot/docs/{version}/reference/html/");
		Release twoFourZeroM1 = this.springBoot.addRelease(Version.of("2.4.0-M1"));
		twoFourZeroM1.setApiDocUrl("https://docs.spring.io/spring-boot/docs/{version}/api/");
		twoFourZeroM1.setRefDocUrl("https://docs.spring.io/spring-boot/docs/{version}/reference/html/");
	}

	@Test
	public void listReleases() throws Exception {
		given(this.metadataService.fetchFullProject(eq("spring-boot"))).willReturn(this.springBoot);
		this.mvc.perform(get("/api/projects/spring-boot/releases").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.releases.length()").value("3"))
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath("_embedded.releases").description("An array of Project Releases"))
								.andWithPrefix("_embedded.releases[]", releasePayload())
								.and(fieldWithPath("_links").description("Links to other resources")),
						links(releasesLinks())));
	}

	@Test
	public void showRelease() throws Exception {
		given(this.metadataService.findRelease(eq("spring-boot"), eq(Version.of("2.3.0.RELEASE")))).willReturn(this.currentRelease);
		this.mvc.perform(get("/api/projects/spring-boot/releases/2.3.0.RELEASE").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						responseFields(releasePayload()), links(releaseLinks())));
	}

	@Test
	public void deleteRelease() throws Exception {
		given(this.metadataService.findRelease(eq("spring-boot"), eq(Version.of("2.3.0.RELEASE")))).willReturn(this.currentRelease);
		this.mvc.perform(delete("/api/projects/spring-boot/releases/2.3.0.RELEASE"))
				.andExpect(status().isNoContent())

				.andDo(document("{method-name}"));
		verify(this.metadataService).save(any());
	}

	@Test
	public void createRelease() throws Exception {
		given(this.metadataService.fetchFullProject(eq("spring-boot"))).willReturn(this.springBoot);
		Map<String, String> newRelease = new HashMap<>();
		newRelease.put("version", "2.2.0.RELEASE");
		newRelease.put("apiDocUrl", "https://docs.spring.io/spring-boot/docs/{version}/api/");
		newRelease.put("referenceDocUrl", "https://docs.spring.io/spring-boot/docs/{version}/reference/html/");
		ConstrainedFields fields = constraintsOn(ReleaseMetadataInput.class);
		this.mvc.perform(post("/api/projects/spring-boot/releases")
				.contentType(MediaType.APPLICATION_JSON).content(this.objectMapper.writeValueAsString(newRelease)))
				.andExpect(status().isCreated())
				.andExpect(header().string("Location", "https://spring.io/api/projects/spring-boot/releases/2.2.0.RELEASE"))
				.andDo(document("{method-name}", preprocessRequest(prettyPrint()), preprocessResponse(prettyPrint()),
						requestFields(
								fields.withPath("version").description("The Release version"),
								fields.withPath("referenceDocUrl").description("URL of the reference documentation, {version} template variable is supported"),
								fields.withPath("apiDocUrl").description("URL of the API documentation, {version} template variable is supported"))));
	}

	FieldDescriptor[] releasePayload() {
		return new FieldDescriptor[] {
				fieldWithPath("version").type(JsonFieldType.STRING).description("Release Version string"),
				fieldWithPath("status").type(JsonFieldType.STRING).description("<<release-status, Status of this Release>>"),
				fieldWithPath("referenceDocUrl").type(JsonFieldType.STRING).description("URL for the reference documentation"),
				fieldWithPath("apiDocUrl").type(JsonFieldType.STRING).description("URL for the API documentation"),
				fieldWithPath("current").type(JsonFieldType.BOOLEAN).description("Whether this release is the most recent, officially supported"),
				fieldWithPath("_links").description("Links to other resources")
		};
	}

	LinkDescriptor[] releasesLinks() {
		return new LinkDescriptor[] {
				linkWithRel("project").description("Link to Project"),
				linkWithRel("current").optional().description("Link to the <<release, Release>> marked as current")
		};
	}

	LinkDescriptor[] releaseLinks() {
		return new LinkDescriptor[] {
				linkWithRel("self").description("Canonical self link"),
				linkWithRel("repository").description("Link to the <<repository, Repository>> hosting this Release")
		};
	}


	@Configuration
	static class ReleaseMetadataTestConfig {

		@Bean
		public ReleaseMetadataAssembler releaseMetadataAssembler(ModelMapper modelMapper) {
			return new ReleaseMetadataAssembler(modelMapper);
		}
	}

}