package sagan.renderer;

import javax.validation.constraints.Pattern;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

/**
 * Configuration properties for the Sagan Renderer application
 */
@ConfigurationProperties("sagan.renderer")
@Validated
public class RendererProperties {

	private final Github github = new Github();

	private final Guides guides = new Guides();

	public Github getGithub() {
		return this.github;
	}

	public Guides getGuides() {
		return this.guides;
	}

	public static class Github {

		/**
		 * Access token to query public github endpoints.
		 * https://developer.github.com/v3/auth/#authenticating-for-saml-sso
		 */
		@Pattern(regexp = "([0-9a-z]*)?")
		private String token;

		public String getToken() {
			return this.token;
		}

		public void setToken(String token) {
			this.token = token;
		}

	}

	public static class Guides {

		/**
		 * Name of the Github organization to fetch guides from.
		 */
		private String organization = "spring-guides";

		public String getOrganization() {
			return this.organization;
		}

		public void setOrganization(String organization) {
			this.organization = organization;
		}
	}
}
