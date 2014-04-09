package sagan.team.support;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.support.PostRepository;
import sagan.team.MemberProfile;
import sagan.team.MemberProfileBuilder;
import saganx.AbstractIntegrationTests;

import java.text.ParseException;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.collection.IsIterableContainingInOrder.contains;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

public class ViewTeamMemberTests extends AbstractIntegrationTests {
    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private PostRepository postRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();
    }

    @Test
    public void getTeamMemberPage() throws Exception {
        MemberProfile profile = new MemberProfile();
        profile.setName("First Last");
        profile.setGithubUsername("someguy");
        profile.setUsername("someguy");

        teamRepository.save(profile);

        mockMvc.perform(get("/team/someguy"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("First Last")))
                .andExpect(content().string(containsString("someguy")));
    }

    @Test
    public void getTeamMemberPage_404sWhenNoMemberFound() throws Exception {
        mockMvc.perform(get("/team/not-a-user")).andExpect(status().isNotFound());
    }

    @Test
    public void getTeamMemberPage_404sWhenMemberIsHidden() throws Exception {
        MemberProfile profile = new MemberProfile();
        profile.setName("Hidden User");
        profile.setGithubUsername("hidden-user");
        profile.setUsername("hidden-user");
        profile.setHidden(true);

        teamRepository.save(profile);

        mockMvc.perform(get("/team/hidden-user")).andExpect(status().isNotFound());
    }

    @Test
    public void getTeamMemberPageShowsPosts() throws Exception {
        MemberProfile profile = new MemberProfile();
        profile.setName("First Last");
        profile.setGithubUsername("someguy");
        profile.setUsername("someguy");

        teamRepository.save(profile);

        Post post = PostBuilder.post().author(profile).title("My Post").build();
        postRepository.save(post);

        mockMvc.perform(get("/team/someguy"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("My Post")));
    }

    @Test
    public void getNonExistentTeamMemberPage() throws Exception {
        mockMvc.perform(get("/team/someguy")).andExpect(status().isNotFound());
    }

    @Test
    public void getTeamMemberPageForNameWithDashes() throws Exception {
        MemberProfile profile = new MemberProfile();
        profile.setName("First Last");
        profile.setGithubUsername("some-guy");
        profile.setUsername("some-guy");

        teamRepository.save(profile);

        mockMvc.perform(get("/team/some-guy"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("First Last")))
                .andExpect(content().string(containsString("some-guy")));
    }

    @Test
    public void givenPostsCreatedOutOfOrder_authorsPostsShownOrderedByPublishedDate() throws Exception {
        MemberProfile activeAuthor = MemberProfileBuilder.profile().username("active_author").build();
        createAuthoredPost(activeAuthor, "Happy New Year", "2013-01-01 00:00");
        createAuthoredPost(activeAuthor, "Back to Work", "2013-01-03 00:00");
        createAuthoredPost(activeAuthor, "Off to the Sales", "2013-01-02 00:00");

        MvcResult response = mockMvc.perform(get("/team/active_author")).andExpect(status().isOk()).andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        List<String> titles = html.select(".member-post--title").stream()
                .map(Element::text).collect(toList());
        assertThat(titles, contains("Back to Work", "Off to the Sales", "Happy New Year"));
    }

    private Post createAuthoredPost(MemberProfile author, String title, String publishAt) throws ParseException {
        Post post = PostBuilder.post().author(author).title(title).publishAt(publishAt).build();
        return postRepository.save(post);
    }

}
