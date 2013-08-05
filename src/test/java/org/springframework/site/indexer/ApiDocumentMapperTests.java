package org.springframework.site.indexer;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.Test;
import org.springframework.core.io.ClassPathResource;
import org.springframework.site.domain.projects.Project;
import org.springframework.site.domain.projects.ProjectVersion;
import org.springframework.site.domain.projects.Version;
import org.springframework.site.indexer.mapper.ApiDocumentMapper;
import org.springframework.site.search.SearchEntry;

import java.io.InputStream;
import java.util.Collections;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

public class ApiDocumentMapperTests {

	private Project project = new Project("spring",
			"Spring",
			"http://www.example.com/repo/spring-framework",
			"http://www.example.com/spring-framework",
			Collections.<ProjectVersion>emptyList());

	private ApiDocumentMapper apiDocumentMapper = new ApiDocumentMapper(project, new Version("3.2.1.RELEASE", Version.Release.CURRENT));

	@Test
	public void mapOlderJdkApiDoc() throws Exception {
		InputStream html = new ClassPathResource("/fixtures/apidocs/apiDocument.html", getClass()).getInputStream();
		Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

		SearchEntry searchEntry = apiDocumentMapper.map(document);
		assertThat(searchEntry.getRawContent(), equalTo("SomeClass"));
	}

	@Test
	public void mapJdk7ApiDoc() throws Exception {
		InputStream html = new ClassPathResource("/fixtures/apidocs/jdk7javaDoc.html", getClass()).getInputStream();
		Document document = Jsoup.parse(html, "UTF-8", "http://example.com/docs");

		SearchEntry searchEntry = apiDocumentMapper.map(document);
		assertThat(searchEntry.getRawContent(), equalTo(document.select(".block").text()));
		assertThat(searchEntry.getType(), equalTo("apiDoc"));
	}
}
