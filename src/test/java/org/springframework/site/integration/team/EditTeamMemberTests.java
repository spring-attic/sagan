package org.springframework.site.integration.team;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.team.MemberProfile;
import org.springframework.site.team.TeamRepository;
import org.springframework.test.configuration.ElasticsearchStubConfiguration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = ElasticsearchStubConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
@Transactional
public class EditTeamMemberTests {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TeamRepository teamRepository;

	private MockMvc mockMvc;
	private Principal principal;

	@Before
	public void setup() {
		principal = new Principal() {
			@Override
			public String getName() {
				return "someguy";
			}
		};
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
	}

	@Test
	public void getEditTeamMemberPageWithNoProfile() throws Exception {
		this.mockMvc.perform(get("/admin/team/myprofile/edit").principal(principal))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("someguy")));
	}

	@Test
	public void getEditTeamMemberPageWithExistingProfile() throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setFirstName("First");
		profile.setLastName("Last");
		profile.setLocation("Location");
		profile.setEmail("test@example.com");
		profile.setGithubUsername("someguy");
		profile.setMemberId("someguy");

		teamRepository.save(profile);

		this.mockMvc.perform(get("/admin/team/myprofile/edit").principal(principal))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First")))
				.andExpect(content().string(containsString("Last")));
	}

	@Test
	public void savingAProfileForTheFirstTime() throws Exception {
		MockHttpServletRequestBuilder requestBuilder = put("/admin/team/myprofile").principal(principal);
		requestBuilder.param("firstName", "Some");
		requestBuilder.param("lastName", "Guy");
		requestBuilder.param("location", "London");
		requestBuilder.param("bio", "I am just a guy");
		requestBuilder.param("email", "someguy@example.com");
		requestBuilder.param("githubUsername", "someguy");

		this.mockMvc.perform(requestBuilder)
				.andExpect(new ResultMatcher() {
					@Override
					public void match(MvcResult result) {
						String redirectedUrl = result.getResponse().getRedirectedUrl();
						MatcherAssert.assertThat(redirectedUrl, startsWith("/admin/team/myprofile/edit"));
					}
				});

		MemberProfile profile = teamRepository.findByMemberId("someguy");

		assertThat(profile, not(nullValue()));

		assertEquals("someguy", profile.getMemberId());
		assertEquals("Some", profile.getFirstName());
		assertEquals("Guy", profile.getLastName());
		assertEquals("London", profile.getLocation());
		assertEquals("I am just a guy", profile.getBio());
		assertEquals("someguy@example.com", profile.getEmail());
		assertEquals("someguy", profile.getGithubUsername());

	}
}
