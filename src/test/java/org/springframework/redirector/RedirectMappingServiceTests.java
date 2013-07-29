package org.springframework.redirector;

import org.junit.Before;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RedirectMappingServiceTests {

	private RedirectMappingService service;
	private String newUrl;

	@Before
	public void setUp() throws Exception {
		Map<String, String> mappings = new HashMap<>();
		newUrl = "http://example.com/new";
		mappings.put("/old", newUrl);
		service = new RedirectMappingService(mappings);
	}

	@Test
	public void testRedirectUrlFor() throws Exception {
		assertThat(service.redirectUrlFor("/old"), equalTo(newUrl));
	}

	@Test
	public void redirectionIgnoresTrailingSlashesFromIncomingRequestPath() throws Exception {
		assertThat(service.redirectUrlFor("/old/"), equalTo(newUrl));
	}
}
