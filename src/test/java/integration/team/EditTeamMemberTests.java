package integration.team;

import integration.configuration.SiteOfflineConfiguration;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.bootstrap.context.initializer.ConfigFileApplicationContextInitializer;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
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
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
@ContextConfiguration(classes = SiteOfflineConfiguration.class, initializers = ConfigFileApplicationContextInitializer.class)
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
	public void getEditMyProfilePageWithExistingProfile() throws Exception {
		getEditProfilePage("/admin/profile");
	}

	@Test
	public void getEditOtherTeamMemberPageWithExistingProfile() throws Exception {
		getEditProfilePage("/admin/team/someguy");
	}

	private void getEditProfilePage(String editTeamUri) throws Exception {
		MemberProfile profile = new MemberProfile();
		profile.setName("First Last");
		profile.setLocation("Location");
		profile.setGithubUsername("someguy");
		profile.setMemberId("someguy");

		teamRepository.save(profile);

		this.mockMvc.perform(get(editTeamUri).principal(principal))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andExpect(content().string(containsString("First Last")));
	}

	@Test
	public void saveMyProfile() throws Exception {
		saveProfile("/admin/profile");
	}

	@Test
	public void saveOtherTeamMemberProfile() throws Exception {
		saveProfile("/admin/team/someguy");
	}

	private void saveProfile(String editTeamUri) throws Exception {
		MemberProfile existingProfile = new MemberProfile();
		existingProfile.setMemberId("someguy");
		existingProfile.setName("Some");
		existingProfile.setLocation("London");
		existingProfile.setBio("I am just a guy");
		existingProfile.setGithubUsername("someguy");
		existingProfile.setTwitterUsername("tw_someguy");
		existingProfile.setSpeakerdeckUsername("sd_someguy");
		existingProfile.setLanyrdUsername("ly_someguy");
		teamRepository.save(existingProfile);

		MockHttpServletRequestBuilder requestBuilder = put(editTeamUri).principal(principal);
		requestBuilder.param("name", "Some_ Guy_");
		requestBuilder.param("location", "London_");
		requestBuilder.param("bio", "I am just a guy_");
		requestBuilder.param("githubUsername", "gh_someguy_");
		requestBuilder.param("twitterUsername", "tw_someguy_");
		requestBuilder.param("speakerdeckUsername", "sd_someguy_");
		requestBuilder.param("lanyrdUsername", "ly_someguy_");
		requestBuilder.param("geoLocation", "-12.5,45.3");
		requestBuilder.param("videoEmbeds", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/J---aiyznGQ\" frameborder=\"0\" allowfullscreen></iframe>");

		performRequestAndExpectRedirect(requestBuilder, editTeamUri);

		MemberProfile profile = teamRepository.findByMemberId("someguy");
		assertThat(profile, not(nullValue()));
		assertEquals("someguy", profile.getMemberId());
		assertEquals("Some_ Guy_", profile.getName());
		assertEquals("London_", profile.getLocation());
		assertEquals("I am just a guy_", profile.getBio());
		assertEquals("gh_someguy_", profile.getGithubUsername());
		assertEquals("tw_someguy_", profile.getTwitterUsername());
		assertEquals("sd_someguy_", profile.getSpeakerdeckUsername());
		assertEquals("ly_someguy_", profile.getLanyrdUsername());
		assertEquals("<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/J---aiyznGQ\" frameborder=\"0\" allowfullscreen></iframe>", profile.getVideoEmbeds());

		assertThat(profile.getGeoLocation(), not(nullValue()));
		assertThat((double)profile.getGeoLocation().getLatitude(), closeTo(-12.5, 0.1));
		assertThat((double) profile.getGeoLocation().getLongitude(), closeTo(45.3, 0.1));
	}

	private void performRequestAndExpectRedirect(MockHttpServletRequestBuilder requestBuilder, final String expectedRedirectUrl) throws Exception {
		this.mockMvc.perform(requestBuilder)
				.andExpect(new ResultMatcher() {
					@Override
					public void match(MvcResult result) {
						String redirectedUrl = result.getResponse().getRedirectedUrl();
						MatcherAssert.assertThat(redirectedUrl, startsWith(expectedRedirectUrl));
					}
				});
	}

}
