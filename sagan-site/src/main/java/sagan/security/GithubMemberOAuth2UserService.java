package sagan.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
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
import org.springframework.stereotype.Component;
import org.springframework.util.Assert;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestOperations;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Adds a ROLE_ADMIN Granted Authority to users who are members of the provided GitHub
 * team by checking against the
 * <a href="https://developer.github.com/v3/teams/members/#get-team-member">GitHub
 * membership API</a>
 *
 * We should eventually update this to use the new
 * <a href="https://developer.github.com/v3/teams/members/#get-team-membership">membership
 * API</a> since the members API is deprecated.
 *
 * @author Rob Winch
 */
@Component
public class GithubMemberOAuth2UserService extends DefaultOAuth2UserService {
    private static final String MEMBER_PATH_TEMPLATE = "/teams/{teamId}/members/{username}";

    private String memberUrlTemplate = "https://api.github.com" + MEMBER_PATH_TEMPLATE;

    private RestOperations rest = new RestTemplateBuilder().build();

    private final String gitHubTeamId;

    public GithubMemberOAuth2UserService(
            @Value("${github.team.id}") String gitHubTeamId) {
        this.gitHubTeamId = gitHubTeamId;
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
        headers.setBearerAuth(accessToken);
        try {
            @SuppressWarnings("rawtypes") ResponseEntity<Map> response = this.rest
                    .exchange(this.memberUrlTemplate, HttpMethod.GET,
                            new HttpEntity<>(headers), Map.class, this.gitHubTeamId,
                            userName);
            return response.getStatusCode().is2xxSuccessful();
        } catch (HttpClientErrorException.NotFound notFound) {
            return false;
        }
    }

    public void setMemberHost(String memberHost) {
        Assert.notNull(memberHost, "memberHost cannot be null");
        this.memberUrlTemplate = memberHost + MEMBER_PATH_TEMPLATE;
    }
}
