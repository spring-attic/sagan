package org.springframework.redirector;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.Map;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

public class RedirectMappingParserTests {

	@Test
	public void parsesMappings() throws Exception {
		String file =
				"---\n" +
				"mappings:\n" +
				"  - old_url: http://example.com/old\n" +
				"    new_url: http://example.com/new\n";

		InputStream yaml = new ByteArrayInputStream(file.getBytes("UTF-8"));
		RedirectMappingParser parser = new RedirectMappingParser();
		Map<String, String> mappingEntries = parser.parseMappings(yaml);
		assertThat(mappingEntries.size(), equalTo(1));
		assertThat(mappingEntries.get("/old"), equalTo("http://example.com/new"));
	}

	@Test
	public void stripsTrailingSlashesOnOldUrl() throws Exception {
		String file =
				"---\n" +
				"mappings:\n" +
				"  - old_url: http://example.com/some/foo/\n" +
				"    new_url: http://example.com/new\n";

		InputStream yaml = new ByteArrayInputStream(file.getBytes("UTF-8"));
		RedirectMappingParser parser = new RedirectMappingParser();
		Map<String, String> mappingEntries = parser.parseMappings(yaml);
		assertThat(mappingEntries.size(), equalTo(1));
		assertThat(mappingEntries.get("/some/foo"), equalTo("http://example.com/new"));
	}


}
