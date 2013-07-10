package org.springframework.site.web.security;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

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
		when(connection.getDisplayName()).thenReturn("dsyer");
		assertEquals("dsyer", signup.execute(connection));
	}

	@Test
	public void transferNullToUserId() {
		when(connection.getDisplayName()).thenReturn(null);
		assertEquals(null, signup.execute(connection));
	}

}