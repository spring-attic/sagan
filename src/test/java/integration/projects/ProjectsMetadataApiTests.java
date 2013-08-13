package integration.projects;

import integration.IntegrationTestBase;
import org.junit.Test;
import org.springframework.boot.config.JacksonJsonParser;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static junit.framework.TestCase.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.greaterThan;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ProjectsMetadataApiTests extends IntegrationTestBase {

	@Test
	public void projectMetadata_respondsWithJavascript() throws Exception {
		mockMvc.perform(MockMvcRequestBuilders.get("/projects/spring-framework?callback=a_function_name"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/javascript"));
	}

	@Test
	public void projectMetadata_respondsWithCallback_andData() throws Exception {
		MvcResult result = mockMvc.perform(MockMvcRequestBuilders.get("/projects/spring-framework?callback=a_function_name"))
				.andReturn();

		String content = result.getResponse().getContentAsString();

		String functionNameRegex = "^([^(]*)\\((.*)\\);$";
		Matcher matcher = Pattern.compile(functionNameRegex).matcher(content);
		if (matcher.find()) {
			assertThat(matcher.group(1), equalTo("a_function_name"));

			Map<String, Object> projectMetadata = new JacksonJsonParser().parseMap(matcher.group(2));
			assertThat(projectMetadata.size(), greaterThan(0));

			assertThat((String) projectMetadata.get("name"), equalTo("Spring Framework"));
			List<Object> releases = (List<Object>) projectMetadata.get("projectReleases");
			Map<String, Object> release = (Map<String, Object>) releases.get(0);
			assertThat((String) release.get("fullName"), equalTo("4.0.0.M2"));
			assertThat((String) release.get("refDocUrl"), equalTo("http://docs.springframework.io/spring/docs/4.0.0.M2/spring-framework-reference/html/"));
			assertThat((String) release.get("apiDocUrl"), equalTo("http://docs.springframework.io/spring/docs/4.0.0.M2/javadoc-api/"));
			assertThat((Boolean) release.get("preRelease"), equalTo(true));
			assertThat((Boolean) release.get("current"), equalTo(false));
			assertThat((Boolean) release.get("supported"), equalTo(false));

			release = (Map<String, Object>) releases.get(1);
			assertThat((String) release.get("fullName"), equalTo("3.2.4.RELEASE"));
			assertThat((String) release.get("refDocUrl"), equalTo("http://docs.springframework.io/spring/docs/3.2.4.RELEASE/spring-framework-reference/html/"));
			assertThat((String) release.get("apiDocUrl"), equalTo("http://docs.springframework.io/spring/docs/3.2.4.RELEASE/javadoc-api/"));
			assertThat((Boolean) release.get("preRelease"), equalTo(false));
			assertThat((Boolean) release.get("current"), equalTo(true));
			assertThat((Boolean) release.get("supported"), equalTo(false));

			release = (Map<String, Object>) releases.get(2);
			assertThat((String) release.get("fullName"), equalTo("3.1.4.RELEASE"));
			assertThat((String) release.get("refDocUrl"), equalTo("http://docs.springframework.io/spring/docs/3.1.4.RELEASE/spring-framework-reference/html/"));
			assertThat((String) release.get("apiDocUrl"), equalTo("http://docs.springframework.io/spring/docs/3.1.4.RELEASE/javadoc-api/"));
			assertThat((Boolean) release.get("preRelease"), equalTo(false));
			assertThat((Boolean) release.get("current"), equalTo(false));
			assertThat((Boolean) release.get("supported"), equalTo(true));
		}
		else {
			fail(String.format("no match found: %s", content));
		}
	}
}
