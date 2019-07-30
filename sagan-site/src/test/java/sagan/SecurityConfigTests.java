package sagan;

import sagan.security.GithubMemberOAuth2UserService;

import org.junit.Test;
import org.junit.runner.RunWith;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.oauth2.client.servlet.OAuth2ClientAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.head;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * @author Rob Winch
 */

@RunWith(SpringRunner.class)
@WebMvcTest(controllers = SecurityConfigTests.TestController.class)
@TestPropertySource(properties = "spring.profiles.active=standalone")
public class SecurityConfigTests {
	@Autowired
	MockMvc mockMvc;

	@Test
	public void httpWhenProductionHeaderThenRedirectsToHttps() throws Exception {
		this.mockMvc.perform(get("/").header("x-forwarded-port", "443"))
			.andExpect(redirectedUrl("https://localhost/"));
	}

	@Test
	public void homePageWhenAnonymousThenOk() throws Exception {
		this.mockMvc.perform(get("/"))
				.andExpect(status().isOk());
	}

	@Test
	public void adminWhenAnonymousThenSigninRequired() throws Exception {
		this.mockMvc.perform(get("/admin/"))
				.andExpect(redirectedUrl("http://localhost/signin"));
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void adminWhenAdminThenOk() throws Exception {
		this.mockMvc.perform(get("/admin/"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser
	public void adminWhenUserThenForbidden() throws Exception {
		this.mockMvc.perform(get("/admin/"))
				.andExpect(status().isForbidden());
	}

	@Test
	// this should match the link in signin.html
	public void authorizationWhenGithubThenRequestsToken() throws Exception {
		this.mockMvc.perform(get("/oauth2/authorization/github")).andDo(result -> {
			String redirectedUrl = result.getResponse().getRedirectedUrl();
			assertThat(redirectedUrl).startsWith("https://github.com/login/oauth/authorize");
		});
	}

	@Test
	public void projectMetadataWhenGetThenNoAuthenticationRequired() throws Exception {
		this.mockMvc.perform(get("/project_metadata/spring-security"))
				.andExpect(status().isOk());
	}

	@Test
	public void projectMetadataWhenHeadThenNoAuthenticationRequired() throws Exception {
		this.mockMvc.perform(head("/project_metadata/spring-security"))
				.andExpect(status().isOk());
	}

	@Test
	public void projectMetadataWhenPostAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(post("/project_metadata/spring-security"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void projectMetadataWhenPutAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(put("/project_metadata/spring-security"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	public void projectMetadataWhenDeleteAndAnonymousThenAuthenticationRequired() throws Exception {
		this.mockMvc.perform(delete("/project_metadata/spring-security"))
				.andExpect(status().is4xxClientError());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenPostAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(post("/project_metadata/spring-security"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenPutAndAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(put("/project_metadata/spring-security"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = "ADMIN")
	public void projectMetadataWhenDeleteAndAdminThenForbidden() throws Exception {
		this.mockMvc.perform(delete("/project_metadata/spring-security"))
				.andExpect(status().isForbidden());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenPostAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(post("/project_metadata/spring-security"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenPutAndAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(put("/project_metadata/spring-security"))
				.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(roles = {"API", "ADMIN"})
	public void projectMetadataWhenDeleteAndApiAdminThenOk() throws Exception {
		this.mockMvc.perform(delete("/project_metadata/spring-security"))
				.andExpect(status().isOk());
	}

	@RestController
	@TestConfiguration
	@Import({OAuth2ClientAutoConfiguration.class, GithubMemberOAuth2UserService.class})
	static class TestController {
		@RequestMapping("/**")
		String ok() {
			return "ok";
		}
	}
}