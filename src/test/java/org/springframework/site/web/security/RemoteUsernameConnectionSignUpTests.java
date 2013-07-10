package org.springframework.site.web.security;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

import static org.junit.Assert.*;
import static org.mockito.BDDMockito.*;

/**
 * Simple {@link ConnectionSignUp} implementation that pulls user id from remote
 * user details in the Social {@link Connection}.
 * 
 * @author Dave Syer
 * 
 */
public class RemoteUsernameConnectionSignUpTests {
	
	private RemoteUsernameConnectionSignUp signup = new RemoteUsernameConnectionSignUp();
	private Connection<?> connection = Mockito.mock(Connection.class);

	@Test
	public void transferDisplayNameToUserId() {
		given(connection.getDisplayName()).willReturn("dsyer");
		assertEquals("dsyer", signup.execute(connection));
	}

	@Test
	public void transferNullToUserId() {
		given(connection.getDisplayName()).willReturn(null);
		assertEquals(null, signup.execute(connection));
	}

}