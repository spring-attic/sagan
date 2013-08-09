package integration.team;

import integration.IntegrationTestBase;
import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.site.domain.team.MemberProfile;
import org.springframework.site.domain.team.TeamRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.ResultMatcher;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.security.Principal;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.Matchers.closeTo;
import static org.hamcrest.Matchers.not;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class EditTeamMemberTests extends IntegrationTestBase {
	@Autowired
	private WebApplicationContext wac;

	@Autowired
	private TeamRepository teamRepository;

	private MockMvc mockMvc;
	private Principal principal;

	@Before
	public void setup() {
		MemberProfile existingProfile = new MemberProfile();
		existingProfile.setUsername("some-guy");
		existingProfile.setName("Some");
		existingProfile.setLocation("London");
		existingProfile.setBio("I am just a guy");
		existingProfile.setGithubUsername("gh-some-guy");
		existingProfile.setTwitterUsername("tw_some-guy");
		existingProfile.setSpeakerdeckUsername("sd_some-guy");
		existingProfile.setLanyrdUsername("ly_some-guy");

		final MemberProfile memberProfile = teamRepository.save(existingProfile);

		principal = new Principal() {
			@Override
			public String getName() {
				return memberProfile.getId().toString();
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
		getEditProfilePage("/admin/team/some-guy");
	}

	private void getEditProfilePage(String editTeamUri) throws Exception {
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
		saveProfile("/admin/team/some-guy");
	}

	private void saveProfile(String editTeamUri) throws Exception {
		MockHttpServletRequestBuilder requestBuilder = put(editTeamUri).principal(principal);
		requestBuilder.param("name", "Some_ Guy_");
		requestBuilder.param("location", "London_");
		requestBuilder.param("bio", "I am just a guy_");
		requestBuilder.param("twitterUsername", "tw_some-guy_");
		requestBuilder.param("speakerdeckUsername", "sd_some-guy_");
		requestBuilder.param("lanyrdUsername", "ly_some-guy_");
		requestBuilder.param("geoLocation", "-12.5,45.3");
		requestBuilder.param("videoEmbeds", "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/J---aiyznGQ\" frameborder=\"0\" allowfullscreen></iframe>");

		performRequestAndExpectRedirect(requestBuilder, editTeamUri);

		MemberProfile profile = teamRepository.findByUsername("some-guy");
		assertThat(profile, not(nullValue()));
		assertEquals("some-guy", profile.getUsername());
		assertEquals("gh-some-guy", profile.getGithubUsername());
		assertEquals("Some_ Guy_", profile.getName());
		assertEquals("London_", profile.getLocation());
		assertEquals("I am just a guy_", profile.getBio());
		assertEquals("tw_some-guy_", profile.getTwitterUsername());
		assertEquals("sd_some-guy_", profile.getSpeakerdeckUsername());
		assertEquals("ly_some-guy_", profile.getLanyrdUsername());
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
