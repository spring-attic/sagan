package org.springframework.site.web.security;

import static org.junit.Assert.assertEquals;

import org.junit.After;
import org.junit.Test;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.List;

public class SecurityContextAuthenticationFilterTests {

	private SecurityContextAuthenticationFilter filter = new SecurityContextAuthenticationFilter("/foo");

	@After
	public void clean() {
		SecurityContextHolder.clearContext();
	}

	@Test
	public void testSuccessfulAuthentication() throws Exception {
		List<GrantedAuthority> roleUser = AuthorityUtils.commaSeparatedStringToAuthorityList("ROLE_USER");
		UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken("Nick","N/A",roleUser);
		SecurityContextHolder.getContext().setAuthentication(authentication);

		assertEquals(authentication, filter.attemptAuthentication(null, null));
	}

	@Test
	public void testUnsuccessfulAuthentication() throws Exception {
		assertEquals(null, filter.attemptAuthentication(null, null));
	}

}