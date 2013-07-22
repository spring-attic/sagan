package org.springframework.site.indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.search.SearchEntry;

import java.io.InputStream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ApiDocumentProcessorTests {

	@Test
	public void mapDocumentWithValidClassMarkers() throws Exception {
		InputStream html = new ClassPathResource("/apiDocument.html", getClass()).getInputStream();
		Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

		ApiDocumentMapper apiDocumentProcessor = new ApiDocumentMapper();
		SearchEntry searchEntry = apiDocumentProcessor.map(document);
		assertThat(searchEntry.getRawContent(), equalTo("SomeClass"));
	}

	@Test
	public void mapDocumentWithInvalidClassMarkers() throws Exception {
		InputStream html = new ClassPathResource("/invalidApiDocument.html", getClass()).getInputStream();
		Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

		ApiDocumentMapper apiDocumentProcessor = new ApiDocumentMapper();
		SearchEntry searchEntry = apiDocumentProcessor.map(document);
		assertThat(searchEntry.getRawContent(), equalTo(document.text()));
	}
}
