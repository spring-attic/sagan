package sagan.site.webapi;

import org.junit.jupiter.api.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.MediaTypes;
import org.springframework.restdocs.hypermedia.LinkDescriptor;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.linkWithRel;
import static org.springframework.restdocs.hypermedia.HypermediaDocumentation.links;
import static org.springframework.restdocs.mockmvc.MockMvcRestDocumentation.document;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessRequest;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.preprocessResponse;
import static org.springframework.restdocs.operation.preprocess.Preprocessors.prettyPrint;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Tests for {@link IndexController}
 */
@WebApiTest(IndexController.class)
public class IndexControllerTests {

	@Autowired
	private MockMvc mvc;

	@Test
	public void showIndex() throws Exception {
		this.mvc.perform(get("/api").accept(MediaTypes.HAL_JSON))
				.andExpect(status().isOk())
				.andDo(document("{method-name}", preprocessRequest(prettyPrint()),
						preprocessResponse(prettyPrint()), links(indexLinks())));
	}

	LinkDescriptor[] indexLinks() {
		return new LinkDescriptor[] {
				linkWithRel("projects").optional().description("Link to <<project, Project resources>>"),
				linkWithRel("repositories").optional().description("Link to <<repository, Repository resources>>")
		};
	}

}