package sagan.site.support.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import sagan.site.team.support.TeamService;

import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.client.authentication.OAuth2LoginAuthenticationToken;
import org.springframework.stereotype.Component;

/**
 *
 */
@Component
public class TeamMemberAuthenticationHandler {

	private static final Logger logger = LoggerFactory.getLogger(TeamMemberAuthenticationHandler.class);

	private final TeamService teamService;

	public TeamMemberAuthenticationHandler(TeamService teamService) {
		this.teamService = teamService;
	}

	@EventListener
	public void onSuccess(AuthenticationSuccessEvent success) {
		Authentication authentication = success.getAuthentication();
		if (authentication instanceof OAuth2LoginAuthenticationToken) {
			OAuth2LoginAuthenticationToken oauth = (OAuth2LoginAuthenticationToken) authentication;
			logger.info("OAuth auth success for {}", oauth.getPrincipal().getAttribute("login").toString());
			if(oauth.getAuthorities().stream().map(GrantedAuthority::getAuthority).anyMatch("ROLE_ADMIN"::equals)) {
				Integer id = oauth.getPrincipal().getAttribute("id");
				logger.info("Authenticated user {}", oauth.getPrincipal().getAttribute("login").toString());
				this.teamService.createOrUpdateMemberProfile(Integer.toUnsignedLong(id), oauth.getPrincipal());
			}
		}
	}

}
