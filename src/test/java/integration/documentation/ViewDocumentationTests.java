package integration.documentation;

import integration.IntegrationTestBase;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewDocumentationTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getDocumentationPage() throws Exception {
		this.mockMvc
				.perform(get("/documentation"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("Spring Security")))
				.andExpect(
						content()
								.string(containsString("http://static.springsource.org/spring-mobile/docs/1.0.1.RELEASE/reference/htmlsingle/")));
	}

	@Test
	public void getDocumentationForProjectWithMissingLinks() throws Exception {
		this.mockMvc
				.perform(get("/documentation"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("Spring AMQP")))
				.andExpect(
						content()
								.string(not(containsString("http://static.springsource.org/spring-amqp-samples"))));
	}

}