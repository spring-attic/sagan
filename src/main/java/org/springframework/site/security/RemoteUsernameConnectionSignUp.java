package org.springframework.site.security;

import org.springframework.social.connect.Connection;
import org.springframework.social.connect.ConnectionSignUp;

/**
 * Simple {@link ConnectionSignUp} implementation that pulls user id from remote
 * user details in the Social {@link Connection}.
 * 
 * @author Dave Syer
 * 
 */
public class RemoteUsernameConnectionSignUp implements ConnectionSignUp {
	@Override
	public String execute(Connection<?> connection) {
		return connection.getDisplayName() != null ? connection
				.getDisplayName() : null;
	}
}