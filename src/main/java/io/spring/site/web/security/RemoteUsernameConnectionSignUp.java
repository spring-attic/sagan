package io.spring.site.web.security;

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
        return connection.getKey().getProviderUserId() != null ? connection.getKey().getProviderUserId() : null;
    }
}