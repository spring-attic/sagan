package sagan;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sagan.site")
public class SiteProperties {

	private Cache cache = new Cache();

	private Renderer renderer = new Renderer();

	private Events events = new Events();

	private GitHub gitHub = new GitHub();

	public Renderer getRenderer() {
		return this.renderer;
	}

	public Cache getCache() {
		return this.cache;
	}

	public Events getEvents() {
		return this.events;
	}

	public GitHub getGithub() {
		return this.gitHub;
	}

	public static class Cache {

		/**
		 * Cache duration for guides metadata.
		 */
		private Long listTimeToLive = 3600L;

		/**
		 * Cache duration for guides content.
		 */
		private Long contentTimeToLive = 0L;

		public Long getListTimeToLive() {
			return listTimeToLive;
		}

		public void setListTimeToLive(Long listTimeToLive) {
			this.listTimeToLive = listTimeToLive;
		}

		public Long getContentTimeToLive() {
			return contentTimeToLive;
		}

		public void setContentTimeToLive(Long contentTimeToLive) {
			this.contentTimeToLive = contentTimeToLive;
		}
	}

	public static class Renderer {

		/**
		 * Remote service for rendering text markup as HTML content
		 * and fetching guides content.
		 */
		private String serviceUrl = "http://localhost:8081";

		public String getServiceUrl() {
			return this.serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}
	}

	public static class Events {

		/**
		 * URL to the ICS calendar for Spring events.
		 */
		private String calendarUri;

		public String getCalendarUri() {
			return this.calendarUri;
		}

		public void setCalendarUri(String calendarUri) {
			this.calendarUri = calendarUri;
		}
	}

	public static class GitHub {

		/**
		 * GitHub org that holds the team admin users should belong to.
		 */
		private String org;

		/**
		 * GitHub team admin users should belong to.
		 * @see <a href="https://developer.github.com/v3/teams/members/#get-team-membership-for-a-user">GitHub API team membership</a>
		 */
		private String team;

		/**
		 * Personal access token to use the GitHub API with better rate-limiting.
		 */
		private String accessToken;

		public String getOrg() {
			return this.org;
		}

		public void setOrg(String org) {
			this.org = org;
		}

		public String getTeam() {
			return this.team;
		}

		public void setTeam(String team) {
			this.team = team;
		}

		public String getAccessToken() {
			return this.accessToken;
		}

		public void setAccessToken(String accessToken) {
			this.accessToken = accessToken;
		}
	}

}
