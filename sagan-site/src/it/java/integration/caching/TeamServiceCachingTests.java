package integration.caching;

import sagan.blog.Post;
import sagan.blog.PostBuilder;
import sagan.blog.service.BlogService;
import sagan.blog.view.PostViewFactory;
import sagan.team.MemberProfile;
import sagan.team.MemberProfileBuilder;
import sagan.team.service.TeamService;
import sagan.util.SetSystemProperty;
import sagan.util.web.PageableFactory;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import integration.AbstractIntegrationTests;

import static integration.caching.TeamServiceCachingTests.TestConfiguration;
import static org.mockito.BDDMockito.given;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

/**
 * Integration tests ensuring that caching functionality works as expected in
 * {@link TeamService}.
 *
 * @author Pivotal Labs
 */
@ContextConfiguration(classes = TestConfiguration.class)
public class TeamServiceCachingTests extends AbstractIntegrationTests {

    private MemberProfile memberProfile;

    @Configuration
    public static class TestConfiguration {
        @Bean
        @Primary
        public TeamService mockTeamService() {
            return mock(TeamService.class);
        }

        @Bean
        @Primary
        public BlogService mockBlogService() {
            return mock(BlogService.class);
        }

        @Bean
        @Primary
        public PostViewFactory mockPostViewFactory() {
            return mock(PostViewFactory.class);
        }
    }

    @ClassRule
    public static SetSystemProperty timeToLive = new SetSystemProperty("cache.database.timetolive", "10");

    @Autowired
    private TeamService teamService;

    @Autowired
    private BlogService blogService;

    private PageImpl<Post> pageOfPosts;

    @Before
    public void setUp() throws Exception {
        reset(teamService);
        memberProfile = MemberProfileBuilder.profile().build();
        given(teamService.fetchMemberProfileUsername("someguy")).willReturn(memberProfile);

        reset(blogService);
        List<Post> posts = Arrays.asList(PostBuilder.post().build());
        pageOfPosts = new PageImpl<>(posts, PageableFactory.forLists(1), 1);
        given(blogService.getPublishedPostsForMember(eq(memberProfile), any(Pageable.class))).willReturn(pageOfPosts);
    }

    @Test
    public void cachingListTeamMembers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        verify(teamService, times(1)).fetchActiveMembers();
    }

    @Test
    public void cachingTeamMemberProfiles() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        verify(teamService, times(1)).fetchMemberProfileUsername("someguy");
    }

    @Test
    public void cachingTeamMemberProfilePosts() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        verify(blogService, times(1)).getPublishedPostsForMember(eq(memberProfile), any(Pageable.class));
    }

}
