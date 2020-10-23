package sagan.site;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sagan.site")
public class SiteProperties {

	private final Cache cache = new Cache();

	private final Disqus disqus = new Disqus();

	private final Events events = new Events();

	private final GitHub github = new GitHub();

	private final Renderer renderer = new Renderer();


	public Cache getCache() {
		return this.cache;
	}

	public Disqus getDisqus() {
		return this.disqus;
	}

	public Events getEvents() {
		return this.events;
	}

	public GitHub getGithub() {
		return this.github;
	}

	public Renderer getRenderer() {
		return this.renderer;
	}


	public static class Cache {

		/**
		 * Cache duration for list of guides metadata.
		 */
		private Duration guidesTimeToLive = Duration.ofHours(1);

		/**
		 * Cache duration for a guide content.
		 */
		private Duration guideTimeToLive = Duration.ofDays(30);

		/**
		 * Cache duration for guides content.
		 */
		private Duration eventsTimeToLive = Duration.ofHours(2);

		public Duration getGuidesTimeToLive() {
			return guidesTimeToLive;
		}

		public void setGuidesTimeToLive(Duration guidesTimeToLive) {
			this.guidesTimeToLive = guidesTimeToLive;
		}

		public Duration getGuideTimeToLive() {
			return guideTimeToLive;
		}

		public void setGuideTimeToLive(Duration guideTimeToLive) {
			this.guideTimeToLive = guideTimeToLive;
		}

		public Duration getEventsTimeToLive() {
			return eventsTimeToLive;
		}

		public void setEventsTimeToLive(Duration eventsTimeToLive) {
			this.eventsTimeToLive = eventsTimeToLive;
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
		 * Token configured in GitHub webhooks for this application.
		 */
		private String webhookToken = "changeme";

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

		public String getWebhookToken() {
			return this.webhookToken;
		}

		public void setWebhookToken(String webhookToken) {
			this.webhookToken = webhookToken;
		}
	}

	public static final class Disqus {

		/**
		 * Disqus.com subdomain to get the integration script from.
		 */
		private String shortName = "spring-io-localhost";

		public String getShortName() {
			return this.shortName;
		}

		public void setShortName(String shortName) {
			this.shortName = shortName;
		}
	}

}
