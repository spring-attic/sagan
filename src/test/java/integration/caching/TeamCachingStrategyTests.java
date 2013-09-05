package integration.caching;

import integration.IntegrationTestBase;
import io.spring.site.domain.blog.BlogService;
import io.spring.site.domain.team.MemberProfile;
import io.spring.site.domain.team.MemberProfileBuilder;
import io.spring.site.domain.team.TeamService;
import io.spring.site.web.blog.PostViewFactory;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import utils.SetSystemProperty;

import static integration.caching.TeamCachingStrategyTests.TestConfiguration;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ContextConfiguration(classes = { TestConfiguration.class })
public class TeamCachingStrategyTests extends IntegrationTestBase {

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
    public static SetSystemProperty timeToLive = new SetSystemProperty("cache.database.timetolive", "1");

    @Autowired
    private TeamService teamService;

    @Test
    public void cachingListTeamMembers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        verify(teamService, times(1)).fetchActiveMembers();
    }

    @Test
    public void cachingTeamMemberProfiles() throws Exception {
        MemberProfile memberProfile = MemberProfileBuilder.profile().build();
        given(teamService.fetchMemberProfileUsername("someguy")).willReturn(memberProfile);

        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/someguy"));
        verify(teamService, times(1)).fetchMemberProfileUsername("someguy");
    }

}
