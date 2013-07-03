package org.springframework.site.integration;

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
import org.springframework.test.configuration.ElasticsearchStubConfiguration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

public class BasicAcceptanceTests {

	//TODO make this dynamic
	public static final int PORT = 9080;

	private static ConfigurableApplicationContext context;

	private String serverAddress = "http://localhost:" + PORT;

	@BeforeClass
	public static void start() throws Exception {
		Future<ConfigurableApplicationContext> future = Executors
				.newSingleThreadExecutor().submit(
						new Callable<ConfigurableApplicationContext>() {
							@Override
							public ConfigurableApplicationContext call() throws Exception {
								return (ConfigurableApplicationContext) ApplicationConfiguration
										.build(ElasticsearchStubConfiguration.class).run("--server.port=" + PORT, "--spring.database.url=jdbc:hsqldb:mem:acceptancetestdb");
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

	private ResponseEntity<String> doGet(String path) {
		return getRestTemplate().getForEntity(serverAddress + path, String.class);
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
	public void getStyleSheet() throws Exception {
		ResponseEntity<String> response = doGet("/bootstrap/css/bootstrap.css");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getContentType()
				.isCompatibleWith(MediaType.valueOf("text/css")));
	}

	@Test
	public void getDeviceDetectionGuide() throws Exception {
		ResponseEntity<String> response = doGet("/guides/gs/device-detection/content");
		assertEquals(HttpStatus.OK, response.getStatusCode());
		assertTrue(response.getHeaders().getContentType().isCompatibleWith(MediaType.valueOf("text/html")));
		assertTrue(response.getBody().contains("<img"));
	}

	@Test
	public void adminIsSecure() {
		ResponseEntity<String> response = doGet("/admin/blog");
		assertEquals(HttpStatus.OK, response.getStatusCode());

		Document html = Jsoup.parse(response.getBody());
		Element loginButton = html.select(".body--container form button").first();
		assertThat("No login button found", loginButton, is(notNullValue()));
		assertThat(loginButton.text(), is(equalTo("Sign in with github")));
	}

	@Test
	public void userCanSignOut() throws Exception {
		ResponseEntity<String> response = doGet("/signout");
		assertEquals(HttpStatus.OK, response.getStatusCode());
	}
}