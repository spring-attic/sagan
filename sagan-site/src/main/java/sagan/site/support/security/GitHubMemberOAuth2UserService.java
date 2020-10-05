package sagan.site.support.security;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

/**
 * Adds a ROLE_ADMIN Granted Authority to users who are members of the provided GitHub
 * team by checking against the
 * <a href="https://developer.github.com/v3/teams/members/#get-team-membership-for-a-user">GitHub
 * membership API</a>
 */
public class GitHubMemberOAuth2UserService extends DefaultOAuth2UserService {

	private static final ParameterizedTypeReference<Map<String, String>> STRING_MAP =
			new ParameterizedTypeReference<Map<String, String>>() {
			};

	private static final String MEMBER_PATH_TEMPLATE = "https://api.github.com/orgs/{org}/teams/{team}/memberships/{username}";

	private final RestOperations client;

	private final String org;

	private final String team;

	public GitHubMemberOAuth2UserService(RestTemplateBuilder builder, String org, String team) {
		this.org = org;
		this.team = team;
		this.client = builder.build();
		this.setRestOperations(this.client);
	}


	@Override
	public OAuth2User loadUser(OAuth2UserRequest userRequest)
			throws OAuth2AuthenticationException {
		OAuth2User user = super.loadUser(userRequest);
		String username = (String) user.getAttributes().get("login");
		String oauth2Token = userRequest.getAccessToken().getTokenValue();
		boolean isAdmin = isAdmin(username, oauth2Token);
		if (isAdmin) {
			List<GrantedAuthority> authorities = new ArrayList<>(user.getAuthorities());
			authorities.add(new SimpleGrantedAuthority("ROLE_ADMIN"));
			String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails()
					.getUserInfoEndpoint().getUserNameAttributeName();
			return new DefaultOAuth2User(authorities, user.getAttributes(),
					userNameAttributeName);
		}
		return user;
	}

	/**
	 * Determines if the user is a member of the github team provided at construction
	 * time.
	 * @param userName
	 * @param accessToken
	 * @return
	 */
	private boolean isAdmin(String userName, String accessToken) {
		HttpHeaders headers = new HttpHeaders();
		headers.setBasicAuth(userName, accessToken);
		try {
			ResponseEntity<Map<String, String>> response = this.client
					.exchange(MEMBER_PATH_TEMPLATE, HttpMethod.GET,
							new HttpEntity<>(headers), STRING_MAP, this.org, this.team, userName);
			if (response.getStatusCode().is2xxSuccessful()) {
				return response.getBody().get("state").equals("active");
			}
			return false;
		}
		catch (HttpClientErrorException.NotFound notFound) {
			return false;
		}
	}

}
