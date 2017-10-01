package sagan.team.support;

import sagan.team.MemberProfile;
import saganx.AbstractIntegrationTests;

import java.security.Principal;

import org.hamcrest.MatcherAssert;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.Matchers.closeTo;
import static org.junit.Assert.assertEquals;
import static org.springframework.test.util.MatcherAssertionErrors.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sagan.support.SecurityRequestPostProcessors.*;

public class EditTeamMemberTests extends AbstractIntegrationTests {
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
        existingProfile.setJobTitle("Engineer");
        existingProfile.setLocation("London");
        existingProfile.setBio("I am just a guy");
        existingProfile.setGithubUsername("gh-some-guy");
        existingProfile.setTwitterUsername("tw_some-guy");
        existingProfile.setSpeakerdeckUsername("sd_some-guy");
        existingProfile.setLanyrdUsername("ly_some-guy");

        final MemberProfile memberProfile = teamRepository.save(existingProfile);

        principal = () -> memberProfile.getId().toString();
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(memberProfile.getId()).roles("USER"))).build();
    }

    @Test
    public void getEditMyProfilePageWithExistingProfile() throws Exception {
        getEditProfilePage("/admin/profile");
    }

    @Test
    public void getEditOtherTeamMemberPageWithExistingProfile() throws Exception {
        getEditProfilePage("/admin/team/some-guy");
    }

    @Test
    public void getEditTeamMemberPage_404sWhenNoMemberFound() throws Exception {
        mockMvc.perform(get("/admin/team/not-a-user")).andExpect(status().isNotFound());
    }

    private void getEditProfilePage(String editTeamUri) throws Exception {
        mockMvc.perform(get(editTeamUri).principal(principal))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("Name")));
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
        requestBuilder.param("username", "some-guy");
        requestBuilder.param("jobTitle", "Rock Star");
        requestBuilder.param("location", "London_");
        requestBuilder.param("bio", "I am just a guy_");
        requestBuilder.param("twitterUsername", "tw_some-guy_");
        requestBuilder.param("speakerdeckUsername", "sd_some-guy_");
        requestBuilder.param("lanyrdUsername", "ly_some-guy_");
        requestBuilder.param("geoLocation", "-12.5,45.3");
        requestBuilder
                .param("videoEmbeds",
                        "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/J---aiyznGQ\" frameborder=\"0\" allowfullscreen></iframe>");

        performRequestAndExpectRedirect(requestBuilder, editTeamUri);

        MemberProfile profile = teamRepository.findByUsername("some-guy");
        assertThat(profile, not(nullValue()));
        assertEquals("some-guy", profile.getUsername());
        assertEquals("gh-some-guy", profile.getGithubUsername());
        assertEquals("Some_ Guy_", profile.getName());
        assertEquals("Rock Star", profile.getJobTitle());
        assertEquals("London_", profile.getLocation());
        assertEquals("I am just a guy_", profile.getBio());
        assertEquals("tw_some-guy_", profile.getTwitterUsername());
        assertEquals("sd_some-guy_", profile.getSpeakerdeckUsername());
        assertEquals("ly_some-guy_", profile.getLanyrdUsername());
        assertEquals(
                "<iframe width=\"420\" height=\"315\" src=\"//www.youtube.com/embed/J---aiyznGQ\" frameborder=\"0\" allowfullscreen></iframe>",
                profile.getVideoEmbeds());

        assertThat(profile.getGeoLocation(), not(nullValue()));
        assertThat((double) profile.getGeoLocation().getLatitude(), closeTo(-12.5, 0.1));
        assertThat((double) profile.getGeoLocation().getLongitude(), closeTo(45.3, 0.1));
    }

    private void performRequestAndExpectRedirect(MockHttpServletRequestBuilder requestBuilder,
                                                 final String expectedRedirectUrl) throws Exception {
        mockMvc.perform(requestBuilder)
                .andExpect(result -> {
                    String redirectedUrl = result.getResponse().getRedirectedUrl();
                    MatcherAssert.assertThat(redirectedUrl, startsWith(expectedRedirectUrl));
                });
    }

    @Test
    public void getTeamAdminPage() throws Exception {
        mockMvc.perform(get("/admin/team"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Some")));
    }
}
