package org.springframework.site.integration;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

public class BasicAcceptanceTests {

	private static ConfigurableApplicationContext context;

	@BeforeClass
	public static void start() throws Exception {
		Future<ConfigurableApplicationContext> future = Executors
				.newSingleThreadExecutor().submit(
						new Callable<ConfigurableApplicationContext>() {
							@Override
							public ConfigurableApplicationContext call() throws Exception {
								return (ConfigurableApplicationContext) ApplicationConfiguration
										.build().run();
							}
						});
		context = future.get(30, TimeUnit.SECONDS);
	}

	@AfterClass
	public static void stop() {
		if (context != null) {
			context.close();
		}
	}

	@Test
	public void getStyleSheet() throws Exception {
		ResponseEntity<String> response = getRestTemplate().getForEntity(
				"http://localhost:8080/css/application.css", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getContentType()
				.isCompatibleWith(MediaType.valueOf("text/css")));
		assertTrue(response.getBody().contains("#authentication"));
	}

	@Test
	public void getDeviceDetectionGuide() throws Exception {
		ResponseEntity<String> response = getRestTemplate().getForEntity(
				"http://localhost:8080/guides/gs/device-detection/content", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.valueOf("text/html")));
		assertTrue(response.getBody().contains("<img"));
	}

	private RestTemplate getRestTemplate() {
		RestTemplate restTemplate = new RestTemplate();
		restTemplate.setErrorHandler(new DefaultResponseErrorHandler() {
			@Override
			public boolean hasError(ClientHttpResponse response) throws IOException {
				return false;
			}
		});
		return restTemplate;
	}

	@Test
	public void adminIsSecure() {
		ResponseEntity<String> response = getRestTemplate().getForEntity(
				"http://localhost:8080/admin", String.class);
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Document html = Jsoup.parse(response.getBody());
		Element loginButton = html.select("form button").first();
		assertThat("No login button found", loginButton, is(notNullValue()));
		assertThat(loginButton.text(), is(equalTo("Sign in with github")));
	}
}