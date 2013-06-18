package org.springframework.test.integration.blog;

import org.junit.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.site.configuration.ApplicationConfiguration;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.util.concurrent.Callable;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.assertEquals;

public class AnotherCreateBlogPostTests {

	private static ConfigurableApplicationContext context;
	private FirefoxDriver driver;

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

	@After
	public void quitDriver() {
		if (driver != null) {
			driver.quit();
		}
	}


	@Ignore("Fix approach to browser integration tests")
	@Test
	public void createNewBlogPostViaDriver() throws Exception {
		driver = new FirefoxDriver();
		driver.get("http://localhost:8080/admin/blog/new");
		WebElement titleField = driver.findElement(By.name("title"));
		titleField.sendKeys("Post Title");

		WebElement contentField = driver.findElement(By.name("content"));
		contentField.sendKeys("My Content");

		contentField.submit();

		// Check the title of the page
		assertEquals("Post Title", driver.findElement(By.tagName("h1")).getText());
		assertEquals("My Content", driver.findElement(By.tagName("article")).getText());

		//Close the browser
		driver.quit();
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
}