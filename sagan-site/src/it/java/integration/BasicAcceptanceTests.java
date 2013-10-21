package integration;

import integration.configuration.IntegrationTestsConfiguration;

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
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.web.client.DefaultResponseErrorHandler;
import org.springframework.web.client.RestTemplate;

import sagan.util.FreePortFinder;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class BasicAcceptanceTests {

    public static int port;

    private static ConfigurableApplicationContext context;

    private static String serverAddress;

    @BeforeClass
    public static void start() throws Exception {
        port = FreePortFinder.find();
        serverAddress = "http://localhost:" + port;

        Future<ConfigurableApplicationContext> future = Executors
                .newSingleThreadExecutor().submit(
                        new Callable<ConfigurableApplicationContext>() {
                            @Override
                            public ConfigurableApplicationContext call() throws Exception {
                                return (ConfigurableApplicationContext) SpringApplication
                                        .run(IntegrationTestsConfiguration.class,
                                                "--server.port=" + port,
                                                "--spring.profiles.active=acceptance");
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
    public void getStaticPage() throws Exception {
        ResponseEntity<String> response = doGet("/");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType()
                .isCompatibleWith(MediaType.valueOf("text/html")));
    }

    @Test
    public void getStyleSheet() throws Exception {
        ResponseEntity<String> response = doGet("/bootstrap/css/bootstrap.css");
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(response.getHeaders().getContentType()
                .isCompatibleWith(MediaType.valueOf("text/css")));
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