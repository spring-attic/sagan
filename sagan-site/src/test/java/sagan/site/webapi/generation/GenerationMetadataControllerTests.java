package sagan.site.webapi.generation;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import sagan.site.projects.Project;
import sagan.site.projects.ProjectGeneration;
import sagan.site.projects.ProjectMetadataService;
import sagan.site.projects.SupportStatus;
import sagan.site.webapi.WebApiTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Bean;
import org.springframework.hateoas.MediaTypes;
import org.springframework.http.HttpHeaders;
import org.springframework.restdocs.hypermedia.LinksSnippet;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.halLinks;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.restdocs.payload.PayloadDocumentation.subsectionWithPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link GenerationMetadataController}
 */
@WebApiTest(GenerationMetadataController.class)
public class GenerationMetadataControllerTests {

	@Autowired
	private MockMvc mvc;

	@MockBean
	private ProjectMetadataService metadataService;

	private Project springBoot;

	@BeforeEach
	public void setup() {
		this.springBoot = new Project("spring-boot", "Spring Boot");
		this.springBoot.setRepoUrl("https://github.com/spring-projects/spring-boot");
		this.springBoot.setStatus(SupportStatus.ACTIVE);
		ProjectGeneration twoOneX = this.springBoot.addGeneration("2.1.x", LocalDate.parse("2019-01-01"));
		twoOneX.setOssSupportEnforcedEndDate(LocalDate.parse("2020-01-01"));
		twoOneX.setCommercialSupportEnforcedEndDate(LocalDate.parse("2021-01-01"));
		ProjectGeneration twoTwoX = this.springBoot.addGeneration("2.2.x", LocalDate.parse("2020-01-01"));
		twoTwoX.setOssSupportEnforcedEndDate(LocalDate.parse("2021-01-01"));
		twoTwoX.setCommercialSupportEnforcedEndDate(LocalDate.parse("2022-01-01"));
	}

	@Test
	public void listGenerations() throws Exception {
		given(this.metadataService.fetchFullProject(eq("spring-boot"))).willReturn(this.springBoot);
		this.mvc.perform(get("/api/projects/spring-boot/generations").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("Last-Modified", Matchers.notNullValue()))
				.andExpect(jsonPath("$._embedded.generations.length()").value("2"))
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						generationsLinks(),
						responseFields(fieldWithPath("_embedded.generations").description("An array of Project Generations"))
								.andWithPrefix("_embedded.generations[]", generationPayload())
								.and(subsectionWithPath("_links").description("Links to other resources"))
						));
	}

	@Test
	public void cachedListGenerations() throws Exception {
		OffsetDateTime nextWeek = LocalDateTime.now().plusDays(7).atOffset(ZoneOffset.UTC);
		given(this.metadataService.fetchFullProject(eq("spring-boot"))).willReturn(this.springBoot);
		HttpHeaders headers = new HttpHeaders();
		headers.setIfModifiedSince(nextWeek.toEpochSecond() * 1000);
		this.mvc.perform(get("/api/projects/spring-boot/generations").accept(MediaTypes.HAL_JSON).headers(headers))
				.andExpect(status().isNotModified())
				.andExpect(header().string("Last-Modified", Matchers.notNullValue()))
				.andDo(document("{method-name}", preprocessResponse(prettyPrint())));
	}

	@Test
	public void showGeneration() throws Exception {
		given(this.metadataService.fetchFullProject("spring-boot")).willReturn(this.springBoot);
		this.mvc.perform(get("/api/projects/spring-boot/generations/2.1.x").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andExpect(header().string("Last-Modified", Matchers.notNullValue()))
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						generationLinks(), responseFields(generationPayload())));
	}

	FieldDescriptor[] generationPayload() {
		return new FieldDescriptor[] {
				fieldWithPath("name").type(JsonFieldType.STRING).description("Generation Name"),
				fieldWithPath("initialReleaseDate").type(JsonFieldType.STRING).description("Date of the first release for this Generation"),
				fieldWithPath("ossSupportEndDate").type(JsonFieldType.STRING).description("End date of the OSS support"),
				fieldWithPath("commercialSupportEndDate").type(JsonFieldType.STRING).description("End date of the Commercial support"),
				subsectionWithPath("_links").description("Links to other resources")
		};
	}

	LinksSnippet generationsLinks() {
		return links(halLinks(), linkWithRel("project").description("Link to Project"));
	}

	LinksSnippet generationLinks() {
		return links(halLinks(),
				linkWithRel("self").description("Canonical self link"),
				linkWithRel("project").description("Link to Project")
		);
	}


	@TestConfiguration
	static class GenerationMetadataTestConfig {

		@Bean
		public GenerationMetadataAssembler generationMetadataAssembler(ModelMapper modelMapper) {
			return new GenerationMetadataAssembler(modelMapper);
		}
	}

}