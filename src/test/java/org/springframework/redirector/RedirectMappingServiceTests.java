package org.springframework.redirector;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RedirectMappingServiceTests {

	@Test
	public void testRedirectUrlFor() throws Exception {
		RedirectMappingService service = new RedirectMappingService();
		MappingEntry mappingEntry = new MappingEntry();
		mappingEntry.setOldUrl("http://example.com/old");
		String newUrl = "http://example.com/new";
		mappingEntry.setNewUrl(newUrl);
		service.getMappings().add(mappingEntry);

		assertThat(service.redirectUrlFor("/old"), equalTo(newUrl));
	}
}
