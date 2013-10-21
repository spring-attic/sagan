package integration.team;

import integration.IntegrationTestBase;
import sagan.team.MemberProfile;
import sagan.team.service.TeamRepository;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class ViewTeamTests extends IntegrationTestBase {

    @Autowired
    private WebApplicationContext wac;

    @Autowired
    private TeamRepository teamRepository;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }

    @Test
    public void getTeamPageOnlyShowsNotHiddenMembers() throws Exception {
        MemberProfile visible = new MemberProfile();
        visible.setName("First Last");
        visible.setGithubUsername("someguy");
        visible.setUsername("someguy");

        teamRepository.save(visible);

        MemberProfile hidden = new MemberProfile();
        hidden.setName("Other dude");
        hidden.setGithubUsername("dude");
        hidden.setUsername("dude");
        hidden.setHidden(true);

        teamRepository.save(hidden);

        this.mockMvc.perform(get("/team"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andExpect(content().string(containsString("First Last")))
                .andExpect(content().string(containsString("someguy")))
                .andExpect(content().string(not(containsString("dude"))));
    }

}
