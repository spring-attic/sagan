package saganx;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static sagan.support.SecurityRequestPostProcessors.*;

public class AuthenticationTests extends AbstractIntegrationTests {

    @Autowired
    private WebApplicationContext wac;

    private MockMvc mockMvc;

    @Before
    public void setup() {
        mockMvc = MockMvcBuilders.webAppContextSetup(wac)
                .addFilters(springSecurityFilterChain)
                .defaultRequest(get("/").with(csrf()).with(user(123L).roles("USER"))).build();
    }

    @After
    public void clean() {
        SecurityContextHolder.clearContext();
    }

    @Test
    public void showsErrorAlertWhenErrorParameterGiven() throws Exception {
        MvcResult response = mockMvc.perform(get("/signin?error=foo"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        Element alert = html.select(".alert.alert-error").first();

        assertThat("No alert on page", alert, is(notNullValue()));
        assertThat(alert.text(), containsString("You must authenticate and authorize"));
    }

    @Test
    public void doesNotShowErrorAlertWhenNoErrorParameterGiven() throws Exception {
        MvcResult response = mockMvc.perform(get("/signin"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        Element alert = html.select(".alert.alert-error").first();
        assertThat("Unexpected alert on page ", alert, is(nullValue()));
    }

    @Test
    public void showsAuthenticationInformationWhenSignedIn() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                123L,
                "githubusername",
                AuthorityUtils
                        .commaSeparatedStringToAuthorityList("ROLE_USER"));
        SecurityContextHolder.getContext().setAuthentication(authentication);

        MvcResult response = mockMvc.perform(get("/admin/blog/new"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith("text/html"))
                .andReturn();

        Document html = Jsoup.parse(response.getResponse().getContentAsString());
        Element alert = html.select("#authentication").first();
        assertThat("No authentication element found ", alert, is(notNullValue()));

        Element signOutLink = html.select("#authentication a").first();
        assertThat(alert.text(), containsString("Sign out"));
        assertThat(signOutLink.attr("href"), containsString("/signout"));
    }

    @Test
    public void signoutRedirectsToTheHomePage() throws Exception {
        UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                123L,
                "githubusername",
                AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER"));
        SecurityContextHolder
                .getContext()
                .setAuthentication(authentication);

        mockMvc.perform(get("/signout"))
                .andExpect(status().isFound())
                .andExpect(result -> {
                    String redirectedUrl = result.getResponse().getRedirectedUrl();
                    assertThat(redirectedUrl, equalTo("/"));
                });
    }
}
