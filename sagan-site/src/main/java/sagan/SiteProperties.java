package sagan;

import java.net.URI;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("sagan.site")
public class SiteProperties {

	private Cache cache = new Cache();

	private Renderer renderer = new Renderer();

	private Events events = new Events();

	public Renderer getRenderer() {
		return this.renderer;
	}

	public Cache getCache() {
		return this.cache;
	}

	public Events getEvents() {
		return events;
	}

	public static class Cache {

		/**
		 * Cache duration for guides metadata
		 */
		private Long listTimeToLive = 3600L;

		/**
		 * Cache duration for guides content
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
		 * and fetching guides content
		 */
		private String serviceUrl = "http://localhost:8081";

		public String getServiceUrl() {
			return serviceUrl;
		}

		public void setServiceUrl(String serviceUrl) {
			this.serviceUrl = serviceUrl;
		}
	}

	public static class Events {

		private URI calendarUri;

		public URI getCalendarUri() {
			return calendarUri;
		}

		public void setCalendarUri(URI calendarUri) {
			this.calendarUri = calendarUri;
		}
	}

}
