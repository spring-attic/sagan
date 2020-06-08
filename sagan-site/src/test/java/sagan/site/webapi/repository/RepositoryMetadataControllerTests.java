package sagan.site.webapi.repository;

import org.junit.Test;
import org.junit.runner.RunWith;
import sagan.site.webapi.WebApiTest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;
import static org.springframework.restdocs.payload.PayloadDocumentation.responseFields;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link RepositoryMetadataController}
 */
@RunWith(SpringRunner.class)
@WebApiTest(RepositoryMetadataController.class)
public class RepositoryMetadataControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void listRepositories() throws Exception {
		this.mvc.perform(get("/api/repositories").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$._embedded.repositories.length()").value("3"))
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						responseFields(fieldWithPath("_embedded.repositories").description("An array of Repositories"))
								.andWithPrefix("_embedded.repositories[]", repositoryPayload())));
	}

	@Test
	public void showRepository() throws Exception {
		this.mvc.perform(get("/api/repositories/spring-releases").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andDo(document("{method-name}", preprocessResponse(prettyPrint()),
						responseFields(repositoryPayload()), links(repositoryLinks())));
	}

	FieldDescriptor[] repositoryPayload() {
		return new FieldDescriptor[] {
				fieldWithPath("identifier").type(JsonFieldType.STRING).description("Repository Identifier"),
				fieldWithPath("name").type(JsonFieldType.STRING).description("Name of the Repository"),
				fieldWithPath("url").type(JsonFieldType.STRING).description("URL of the Repository"),
				fieldWithPath("snapshotsEnabled").type(JsonFieldType.BOOLEAN).description("Whether SNAPSHOT artifacts are hosted on this Repository"),
				fieldWithPath("_links").description("Links to other resources")
		};
	}

	LinkDescriptor[] repositoryLinks() {
		return new LinkDescriptor[] {
				linkWithRel("self").description("Canonical self link"),
		};
	}


	@Configuration
	static class RepositoryMetadataTestConfig {

		@Bean
		public RepositoryMetadataAssembler repositoryMetadataAssembler() {
			return new RepositoryMetadataAssembler();
		}
	}
}