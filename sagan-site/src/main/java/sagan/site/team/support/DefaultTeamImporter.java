package sagan.site.team.support;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import sagan.SiteProperties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestOperations;

@Service
public class DefaultTeamImporter implements TeamImporter {

	private static final String API_URL_BASE = "https://api.github.com";

	private final TeamService teamService;
	private final String gitHubTeam;
	private final RestOperations rest;

	@Autowired
	public DefaultTeamImporter(TeamService teamService, SiteProperties properties) {
		this.teamService = teamService;
		this.gitHubTeam = properties.getGithub().getTeam();
		this.rest = new RestTemplateBuilder().basicAuthentication(properties.getGithub().getAccessToken(), "").build();
	}

	@Transactional
	public void importTeamMembers() {
		GitHubUser[] users = getGitHubUsers();
		List<Long> userIds = new ArrayList<>();
		for (GitHubUser user : users) {
			userIds.add(user.getId());
			String userName = getNameForUser(user.getLogin());

			teamService.createOrUpdateMemberProfile(user.getId(), user.getLogin(), user.getAvatarUrl(), userName);
		}
		teamService.showOnlyTeamMembersWithIds(userIds);
	}

	private GitHubUser[] getGitHubUsers() {
		String membersUrl = API_URL_BASE + "/teams/{teamId}/members?per_page=100";
		ResponseEntity<GitHubUser[]> entity =
				this.rest.getForEntity(membersUrl, GitHubUser[].class, gitHubTeam);
		return entity.getBody();
	}

	public String getNameForUser(String username) {
		return this.rest
				.getForObject(API_URL_BASE + "/users/{user}", GitHubUser.class, username)
				.getName();
	}
}

@SuppressWarnings("serial")
@JsonIgnoreProperties(ignoreUnknown = true)
class GitHubUser implements Serializable {
	private Long id;
	private String url;
	private String login;
	private String avatarUrl;
	private String gravatarId;
	private String name;
	private String email;
	private Date date;

	public Long getId() { return id; }

	public void setId(Long id) { this.id = id; }

	public String getUrl() { return url; }

	public void setUrl(String url) { this.url = url; }

	/**
	 * @return watcher's GitHub login
	 */
	public String getLogin() { return login; }

	public void setLogin(String login) { this.login = login; }

	@JsonProperty("avatar_url")
	public String getAvatarUrl() { return avatarUrl; }

	public void setAvatarUrl(String avatarUrl) { this.avatarUrl = avatarUrl; }

	@JsonProperty("gravatar_id")
	public String getGravatarId() { return gravatarId; }

	public void setGravatarId(String gravatarId) { this.gravatarId = gravatarId; }

	public String getName() { return name; }

	public void setName(String name) { this.name = name; }

	public String getEmail() { return email; }

	public void setEmail(String email) { this.email = email; }

	public Date getDate() { return date; }

	public void setDate(Date date) { this.date = date; }
}
