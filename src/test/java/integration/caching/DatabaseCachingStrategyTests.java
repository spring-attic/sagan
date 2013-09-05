package integration.caching;

import integration.IntegrationTestBase;
import io.spring.site.domain.team.TeamRepository;
import io.spring.site.domain.team.TeamService;
import org.junit.Before;
import org.junit.ClassRule;
import org.junit.Test;
import org.springframework.aop.framework.Advised;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import utils.SetSystemProperty;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@DirtiesContext
public class DatabaseCachingStrategyTests extends IntegrationTestBase {

    @ClassRule
    public static SetSystemProperty timeToLive = new SetSystemProperty("cache.database.timetolive", "1");

    @Autowired
    private TeamService teamService;

    private TeamRepository teamRepository = mock(TeamRepository.class);

    @Before
    public void setup() throws Exception {
        //TODO this hack is needed as a mock of TeamRepository is not picked up by Spring Injection
        TeamService realTeamService = (TeamService) ((Advised)teamService).getTargetSource().getTarget();
        ReflectionTestUtils.setField(realTeamService, "teamRepository", teamRepository);
    }

    @Test
    public void cachingListTeamMembers() throws Exception {
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        this.mockMvc.perform(MockMvcRequestBuilders.get("/team/"));
        verify(teamRepository, times(1)).findByHiddenOrderByNameAsc(false);
    }

}
