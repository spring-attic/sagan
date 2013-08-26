package integration;

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

import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static requestpostprocessors.SecurityRequestPostProcessors.*;

public class SigninTests extends IntegrationTestBase {

	@Autowired
	private WebApplicationContext wac;

	private MockMvc mockMvc;

	@Before
	public void setup() {
		this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac)
				.addFilters(springSecurityFilterChain)
				.defaultRequest(get("/").with(csrf()).with(user("Nick").roles("USER"))).build();
	}

	@After
	public void clean() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void showsAlertAfterSuccessfulSignOut() throws Exception {
		MvcResult response = mockMvc.perform(get("/signin?signout=success"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		Element alert = html.select(".alert.alert-success").first();

		assertThat("No sign out alert on page", alert, is(notNullValue()));
		assertThat(alert.text(), containsString("Signed out successfully"));
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
				"Nick",
				"N/A",
				AuthorityUtils
						.commaSeparatedStringToAuthorityList("ROLE_USER"));
		SecurityContextHolder
				.getContext()
				.setAuthentication(
						authentication);

		MvcResult response = mockMvc.perform(get("/admin/blog/new"))
				.andExpect(status().isOk())
				.andExpect(content().contentTypeCompatibleWith("text/html"))
				.andReturn();

		Document html = Jsoup.parse(response.getResponse().getContentAsString());
		Element alert = html.select("#authentication").first();
		assertThat("No authentication element found ", alert, is(notNullValue()));
		assertThat(alert.text(), containsString("Nick"));

		Element signOutLink = html.select("#authentication a").first();
		assertThat(alert.text(), containsString("Sign out"));
		assertThat(signOutLink.attr("href"), containsString("/signout"));
	}

}